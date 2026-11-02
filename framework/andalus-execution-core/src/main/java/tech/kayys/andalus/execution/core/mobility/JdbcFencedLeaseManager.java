package tech.kayys.andalus.execution.core.mobility;

import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.FencedLeaseManager;
import tech.kayys.andalus.execution.mobility.FencingToken;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Shared-database fenced lease manager. Requires transactional SELECT FOR UPDATE
 * support and the schema documented in JDBC_FENCED_LEASE_MANAGER.md.
 */
public final class JdbcFencedLeaseManager implements FencedLeaseManager {
    private static final String TABLE = "andalus_execution_lease";
    private final DataSource dataSource;
    private final Clock clock;

    public JdbcFencedLeaseManager(DataSource dataSource) {
        this(dataSource, Clock.systemUTC());
    }

    public JdbcFencedLeaseManager(DataSource dataSource, Clock clock) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public ExecutionLease acquireLease(String executionId, String holderId, Duration duration) {
        identity(executionId, "executionId");
        identity(holderId, "holderId");
        long durationMillis = positiveMillis(duration);
        return tx(c -> {
            Row row = lock(c, executionId);
            Instant now = clock.instant();
            if (row.holder != null && now.toEpochMilli() < row.expires
                    && !row.holder.equals(holderId)) {
                throw new IllegalStateException("Execution lease is still held by " + row.holder);
            }
            long generation;
            try {
                generation = Math.addExact(row.generation, 1L);
            } catch (ArithmeticException exhausted) {
                throw new IllegalStateException("Fencing generation exhausted for " + executionId, exhausted);
            }
            long expiry = expiryMillis(now.toEpochMilli(), durationMillis);
            try (PreparedStatement s = c.prepareStatement("UPDATE " + TABLE
                    + " SET generation=?, holder_id=?, acquired_at_ms=?, expires_at_ms=? WHERE execution_id=?")) {
                s.setLong(1, generation);
                s.setString(2, holderId);
                s.setLong(3, now.toEpochMilli());
                s.setLong(4, expiry);
                s.setString(5, executionId);
                one(s.executeUpdate());
            }
            return new ExecutionLease(executionId, holderId, FencingToken.of(generation),
                    now, Instant.ofEpochMilli(expiry));
        });
    }

    @Override
    public boolean renewLease(String executionId, String holderId, Duration duration) {
        identity(executionId, "executionId");
        identity(holderId, "holderId");
        long durationMillis = positiveMillis(duration);
        return tx(c -> {
            Row row = lock(c, executionId);
            long now = clock.millis();
            if (!active(row, holderId, now)) return false;
            return updateExpiry(c, executionId, holderId, row.generation,
                    expiryMillis(now, durationMillis));
        });
    }

    @Override
    public boolean renewLease(String executionId, String holderId, FencingToken token, Duration duration) {
        identity(executionId, "executionId");
        identity(holderId, "holderId");
        Objects.requireNonNull(token, "token");
        long durationMillis = positiveMillis(duration);
        return tx(c -> {
            Row row = lock(c, executionId);
            long now = clock.millis();
            if (!active(row, holderId, now) || row.generation != token.generation()) return false;
            return updateExpiry(c, executionId, holderId, token.generation(),
                    expiryMillis(now, durationMillis));
        });
    }

    @Override
    public void releaseLease(String executionId, String holderId) {
        identity(executionId, "executionId");
        identity(holderId, "holderId");
        tx(c -> {
            Row row = lock(c, executionId);
            if (holderId.equals(row.holder)) clear(c, executionId, holderId, row.generation);
            return null;
        });
    }

    @Override
    public boolean releaseLease(String executionId, String holderId, FencingToken token) {
        identity(executionId, "executionId");
        identity(holderId, "holderId");
        Objects.requireNonNull(token, "token");
        return tx(c -> {
            Row row = lock(c, executionId);
            if (!active(row, holderId, clock.millis()) || row.generation != token.generation()) return false;
            clear(c, executionId, holderId, token.generation());
            return true;
        });
    }

    @Override
    public boolean validateToken(String executionId, FencingToken token) {
        if (executionId == null || executionId.isBlank() || token == null) return false;
        return tx(c -> {
            Row row = read(c, executionId);
            return row != null && row.holder != null && row.generation == token.generation()
                    && clock.millis() < row.expires;
        });
    }

    @Override
    public Optional<FencingToken> currentToken(String executionId) {
        if (executionId == null || executionId.isBlank()) return Optional.empty();
        return tx(c -> {
            Row row = read(c, executionId);
            return row == null ? Optional.empty() : Optional.of(FencingToken.of(row.generation));
        });
    }

    @Override
    public Optional<ExecutionLease> getLease(String executionId) {
        if (executionId == null || executionId.isBlank()) return Optional.empty();
        return tx(c -> {
            Row row = read(c, executionId);
            if (row == null || row.holder == null || clock.millis() >= row.expires) return Optional.empty();
            return Optional.of(new ExecutionLease(executionId, row.holder, FencingToken.of(row.generation),
                    Instant.ofEpochMilli(row.acquired), Instant.ofEpochMilli(row.expires)));
        });
    }

    private Row lock(Connection c, String id) throws SQLException {
        Row row = select(c, id, true);
        if (row != null) return row;
        Savepoint point = c.setSavepoint();
        try (PreparedStatement s = c.prepareStatement("INSERT INTO " + TABLE
                + " (execution_id,generation,holder_id,acquired_at_ms,expires_at_ms) VALUES (?,0,NULL,0,0)")) {
            s.setString(1, id);
            s.executeUpdate();
            c.releaseSavepoint(point);
        } catch (SQLException e) {
            c.rollback(point);
            if (!constraint(e)) throw e;
        }
        row = select(c, id, true);
        if (row == null) throw new SQLException("Unable to initialize lease counter for " + id);
        return row;
    }

    private Row read(Connection c, String id) throws SQLException {
        return select(c, id, false);
    }

    private Row select(Connection c, String id, boolean lock) throws SQLException {
        String sql = "SELECT generation,holder_id,acquired_at_ms,expires_at_ms FROM " + TABLE
                + " WHERE execution_id=?" + (lock ? " FOR UPDATE" : "");
        try (PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, id);
            try (ResultSet r = s.executeQuery()) {
                return r.next() ? new Row(r.getLong(1), r.getString(2), r.getLong(3), r.getLong(4)) : null;
            }
        }
    }

    private boolean updateExpiry(Connection c, String id, String holder, long generation, long expiry)
            throws SQLException {
        try (PreparedStatement s = c.prepareStatement("UPDATE " + TABLE
                + " SET expires_at_ms=? WHERE execution_id=? AND holder_id=? AND generation=?")) {
            s.setLong(1, expiry);
            s.setString(2, id);
            s.setString(3, holder);
            s.setLong(4, generation);
            return s.executeUpdate() == 1;
        }
    }

    private void clear(Connection c, String id, String holder, long generation) throws SQLException {
        try (PreparedStatement s = c.prepareStatement("UPDATE " + TABLE
                + " SET holder_id=NULL,acquired_at_ms=0,expires_at_ms=0 WHERE execution_id=? AND holder_id=? AND generation=?")) {
            s.setString(1, id);
            s.setString(2, holder);
            s.setLong(3, generation);
            one(s.executeUpdate());
        }
    }

    private static boolean active(Row row, String holder, long now) {
        return holder.equals(row.holder) && now < row.expires;
    }

    private static boolean constraint(SQLException e) {
        for (SQLException x = e; x != null; x = x.getNextException()) {
            if (x.getSQLState() != null && x.getSQLState().startsWith("23")) return true;
        }
        return false;
    }

    private static void one(int count) throws SQLException {
        if (count != 1) throw new SQLException("Expected one lease row update, got " + count);
    }

    private <T> T tx(SqlWork<T> work) {
        try (Connection c = dataSource.getConnection()) {
            boolean auto = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                try { c.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
                if (e instanceof RuntimeException runtime) throw runtime;
                throw new IllegalStateException("JDBC lease operation failed", e);
            } finally {
                try { c.setAutoCommit(auto); } catch (SQLException ignored) { }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to access JDBC lease store", e);
        }
    }

    private static void identity(String value, String name) {
        if (value == null || value.isBlank() || !value.equals(value.trim()))
            throw new IllegalArgumentException(name + " must be non-blank and trimmed");
    }

    private static long positiveMillis(Duration duration) {
        Objects.requireNonNull(duration, "duration");
        if (duration.isZero() || duration.isNegative())
            throw new IllegalArgumentException("duration must be at least one millisecond");
        final long millis;
        try {
            millis = duration.toMillis();
        } catch (ArithmeticException overflow) {
            throw new IllegalArgumentException("duration exceeds the supported millisecond range", overflow);
        }
        if (millis <= 0)
            throw new IllegalArgumentException("duration must be at least one millisecond");
        return millis;
    }

    private static long expiryMillis(long nowMillis, long durationMillis) {
        try {
            return Math.addExact(nowMillis, durationMillis);
        } catch (ArithmeticException overflow) {
            throw new IllegalArgumentException("lease expiry exceeds the supported timestamp range", overflow);
        }
    }

    @FunctionalInterface
    private interface SqlWork<T> { T run(Connection connection) throws SQLException; }

    private record Row(long generation, String holder, long acquired, long expires) { }
}
