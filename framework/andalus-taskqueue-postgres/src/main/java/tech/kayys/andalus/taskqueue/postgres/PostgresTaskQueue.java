package tech.kayys.andalus.taskqueue.postgres;

import tech.kayys.andalus.harness.fabric.*;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PostgreSQL implementation of {@link TaskQueue}.
 *
 * <p>Claims are acquired under row locks using SKIP LOCKED. Claim generations,
 * owner checks, visibility deadlines, and acknowledgement updates are persisted
 * in the same database shared by all queue instances.</p>
 *
 * <p>Delivery is at-least-once. This adapter does not make worker side effects
 * transactional with queue acknowledgement.</p>
 */
public final class PostgresTaskQueue implements TaskQueue {
    private final DataSource dataSource;

    public PostgresTaskQueue(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource cannot be null");
    }

    /**
     * Creates the queue table and claim index if absent. Run through a migration
     * mechanism in production; this method is convenient for integration tests
     * and simple deployments.
     */
    public void ensureSchema() {
        String table = """
                CREATE TABLE IF NOT EXISTS andalus_execution_task (
                    assignment_id VARCHAR(255) PRIMARY KEY,
                    idempotency_key VARCHAR(512) NOT NULL UNIQUE,
                    execution_id VARCHAR(255) NOT NULL,
                    node_id VARCHAR(255) NOT NULL,
                    attempt_number INTEGER NOT NULL CHECK (attempt_number > 0),
                    assigned_worker_id VARCHAR(255) NOT NULL,
                    assignment_fence_generation BIGINT NOT NULL,
                    assignment_status VARCHAR(32) NOT NULL,
                    timeout_millis BIGINT NOT NULL,
                    max_retries INTEGER NOT NULL,
                    available_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    claim_owner VARCHAR(255),
                    claim_until TIMESTAMPTZ,
                    claim_generation BIGINT NOT NULL DEFAULT 0 CHECK (claim_generation >= 0),
                    acknowledged BOOLEAN NOT NULL DEFAULT FALSE,
                    failure_count INTEGER NOT NULL DEFAULT 0 CHECK (failure_count >= 0),
                    failure_category VARCHAR(128),
                    failure_message TEXT,
                    dead_lettered_at TIMESTAMPTZ,
                    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    CHECK ((claim_owner IS NULL) = (claim_until IS NULL))
                )
                """;
        String index = """
                CREATE INDEX IF NOT EXISTS idx_andalus_execution_task_ready
                ON andalus_execution_task (available_at, assignment_id)
                WHERE acknowledged = FALSE
                """;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(table);
            statement.execute("ALTER TABLE andalus_execution_task ADD COLUMN IF NOT EXISTS failure_count INTEGER NOT NULL DEFAULT 0");
            statement.execute("ALTER TABLE andalus_execution_task ADD COLUMN IF NOT EXISTS failure_category VARCHAR(128)");
            statement.execute("ALTER TABLE andalus_execution_task ADD COLUMN IF NOT EXISTS failure_message TEXT");
            statement.execute("ALTER TABLE andalus_execution_task ADD COLUMN IF NOT EXISTS dead_lettered_at TIMESTAMPTZ");
            statement.execute(index);
        } catch (SQLException e) {
            throw storageFailure("Unable to initialize PostgreSQL task queue schema", e);
        }
    }

    @Override
    public void enqueue(String idempotencyKey, ExecutionAssignment assignment) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey cannot be blank");
        }
        Objects.requireNonNull(assignment, "assignment cannot be null");
        String insert = """
                INSERT INTO andalus_execution_task (
                    assignment_id, idempotency_key, execution_id, node_id, attempt_number,
                    assigned_worker_id, assignment_fence_generation, assignment_status,
                    timeout_millis, max_retries
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (idempotency_key) DO NOTHING
                """;
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(insert)) {
                    bindAssignment(statement, idempotencyKey, assignment);
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT assignment_id FROM andalus_execution_task WHERE idempotency_key = ?")) {
                    statement.setString(1, idempotencyKey);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next() || !assignment.id().value().equals(result.getString(1))) {
                            throw new IllegalStateException(
                                    "Idempotency key already belongs to another assignment");
                        }
                    }
                }
                connection.commit();
            } catch (Exception e) {
                rollback(connection, e);
                if (e instanceof RuntimeException runtime) throw runtime;
                throw storageFailure("Unable to enqueue task", (SQLException) e);
            }
        } catch (SQLException e) {
            throw storageFailure("Unable to enqueue task", e);
        }
    }

    @Override
    public List<TaskClaim> claim(WorkerId workerId, int maxTasks, Duration visibilityTimeout) {
        Objects.requireNonNull(workerId, "workerId cannot be null");
        long visibilityMillis = positiveMillis(visibilityTimeout, "visibilityTimeout");
        if (maxTasks < 1) throw new IllegalArgumentException("maxTasks must be positive");

        String select = """
                SELECT assignment_id, execution_id, node_id, attempt_number, assigned_worker_id,
                       assignment_fence_generation, assignment_status, timeout_millis, max_retries
                FROM andalus_execution_task
                WHERE acknowledged = FALSE AND dead_lettered_at IS NULL
                  AND available_at <= CURRENT_TIMESTAMP
                  AND (claim_owner IS NULL OR claim_until <= CURRENT_TIMESTAMP)
                ORDER BY available_at, assignment_id
                LIMIT ?
                FOR UPDATE SKIP LOCKED
                """;
        String update = """
                UPDATE andalus_execution_task
                SET claim_owner = ?, claim_until = CURRENT_TIMESTAMP + (? * INTERVAL '1 millisecond'),
                    claim_generation = claim_generation + 1
                WHERE assignment_id = ?
                RETURNING claim_generation, claim_until, CURRENT_TIMESTAMP
                """;
        List<TaskClaim> claims = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                List<AssignmentRow> rows = new ArrayList<>();
                try (PreparedStatement statement = connection.prepareStatement(select)) {
                    statement.setInt(1, maxTasks);
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) rows.add(readAssignment(result));
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement(update)) {
                    for (AssignmentRow row : rows) {
                        statement.setString(1, workerId.value());
                        statement.setLong(2, visibilityMillis);
                        statement.setString(3, row.id());
                        try (ResultSet result = statement.executeQuery()) {
                            if (result.next()) {
                                Instant claimedAt = result.getTimestamp(3).toInstant();
                                Instant expiresAt = result.getTimestamp(2).toInstant();
                                TaskClaimToken token = new TaskClaimToken(
                                        AssignmentId.of(row.id()), workerId, result.getLong(1));
                                claims.add(new TaskClaim(row.toAssignment(), token, claimedAt, expiresAt));
                            }
                        }
                    }
                }
                connection.commit();
                return List.copyOf(claims);
            } catch (Exception e) {
                rollback(connection, e);
                if (e instanceof RuntimeException runtime) throw runtime;
                throw storageFailure("Unable to claim tasks", (SQLException) e);
            }
        } catch (SQLException e) {
            throw storageFailure("Unable to claim tasks", e);
        }
    }

    @Override
    public boolean acknowledge(TaskClaimToken token) {
        Objects.requireNonNull(token, "token cannot be null");
        String sql = """
                UPDATE andalus_execution_task
                SET acknowledged = TRUE, claim_owner = NULL, claim_until = NULL
                WHERE assignment_id = ? AND claim_owner = ? AND claim_generation = ?
                  AND claim_until > CURRENT_TIMESTAMP AND acknowledged = FALSE
                """;
        return mutateClaim(sql, token, null);
    }

    @Override
    public boolean negativeAcknowledge(TaskClaimToken token, Duration delay) {
        Objects.requireNonNull(token, "token cannot be null");
        long delayMillis = nonNegativeMillis(delay, "delay");
        String sql = """
                UPDATE andalus_execution_task
                SET claim_owner = NULL, claim_until = NULL,
                    available_at = CURRENT_TIMESTAMP + (? * INTERVAL '1 millisecond')
                WHERE assignment_id = ? AND claim_owner = ? AND claim_generation = ?
                  AND claim_until > CURRENT_TIMESTAMP AND acknowledged = FALSE
                """;
        return mutateClaim(sql, token, delayMillis);
    }

    @Override
    public boolean extendVisibility(TaskClaimToken token, Duration extension) {
        Objects.requireNonNull(token, "token cannot be null");
        long extensionMillis = positiveMillis(extension, "extension");
        String sql = """
                UPDATE andalus_execution_task
                SET claim_until = claim_until + (? * INTERVAL '1 millisecond')
                WHERE assignment_id = ? AND claim_owner = ? AND claim_generation = ?
                  AND claim_until > CURRENT_TIMESTAMP AND acknowledged = FALSE
                """;
        return mutateClaim(sql, token, extensionMillis);
    }

    @Override
    public TaskFailureResult fail(TaskClaimToken token, TaskFailureDecision decision) {
        Objects.requireNonNull(token, "token cannot be null");
        Objects.requireNonNull(decision, "decision cannot be null");
        boolean retryable = decision.retryDelay().isPresent();
        long delayMillis = retryable ? nonNegativeMillis(decision.retryDelay().orElseThrow(), "retryDelay") : 0L;
        String sql = """
                UPDATE andalus_execution_task
                SET failure_count = failure_count + 1,
                    failure_category = ?, failure_message = ?,
                    dead_lettered_at = CASE
                        WHEN ? = FALSE OR failure_count >= max_retries THEN CURRENT_TIMESTAMP
                        ELSE NULL END,
                    available_at = CASE
                        WHEN ? = TRUE AND failure_count < max_retries
                            THEN CURRENT_TIMESTAMP + (? * INTERVAL '1 millisecond')
                        ELSE available_at END,
                    claim_owner = NULL, claim_until = NULL
                WHERE assignment_id = ? AND claim_owner = ? AND claim_generation = ?
                  AND claim_until > CURRENT_TIMESTAMP AND acknowledged = FALSE
                  AND dead_lettered_at IS NULL
                RETURNING failure_count, dead_lettered_at
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, decision.category());
            statement.setString(2, decision.message());
            statement.setBoolean(3, retryable);
            statement.setBoolean(4, retryable);
            statement.setLong(5, delayMillis);
            statement.setString(6, token.assignmentId().value());
            statement.setString(7, token.workerId().value());
            statement.setLong(8, token.generation());
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return TaskFailureResult.LOST_CLAIM;
                return result.getTimestamp(2) == null
                        ? TaskFailureResult.RETRY_SCHEDULED : TaskFailureResult.DEAD_LETTERED;
            }
        } catch (SQLException e) {
            throw storageFailure("Unable to record task failure", e);
        }
    }

    @Override
    public List<DeadLetterTask> listDeadLetters(int limit) {
        if (limit < 1) throw new IllegalArgumentException("limit must be positive");
        String sql = """
                SELECT assignment_id, execution_id, node_id, attempt_number, assigned_worker_id,
                       assignment_fence_generation, assignment_status, timeout_millis, max_retries,
                       failure_count, failure_category, failure_message, dead_lettered_at
                FROM andalus_execution_task
                WHERE dead_lettered_at IS NOT NULL
                ORDER BY dead_lettered_at, assignment_id
                LIMIT ?
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet result = statement.executeQuery()) {
                List<DeadLetterTask> deadLetters = new ArrayList<>();
                while (result.next()) {
                    deadLetters.add(new DeadLetterTask(readAssignment(result).toAssignment(),
                            result.getInt("failure_count"), result.getString("failure_category"),
                            result.getString("failure_message"),
                            result.getTimestamp("dead_lettered_at").toInstant()));
                }
                return List.copyOf(deadLetters);
            }
        } catch (SQLException e) {
            throw storageFailure("Unable to list dead-letter tasks", e);
        }
    }

    private boolean mutateClaim(String sql, TaskClaimToken token, Long durationMillis) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            if (durationMillis != null) statement.setLong(index++, durationMillis);
            statement.setString(index++, token.assignmentId().value());
            statement.setString(index++, token.workerId().value());
            statement.setLong(index, token.generation());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw storageFailure("Unable to update task claim", e);
        }
    }

    private static void bindAssignment(PreparedStatement statement, String key,
                                       ExecutionAssignment assignment) throws SQLException {
        statement.setString(1, assignment.id().value());
        statement.setString(2, key);
        statement.setString(3, assignment.executionId().value());
        statement.setString(4, assignment.nodeId().value());
        statement.setInt(5, assignment.attemptId().number());
        statement.setString(6, assignment.workerId().value());
        statement.setLong(7, assignment.fence().generation());
        statement.setString(8, assignment.status().name());
        statement.setLong(9, assignment.constraints().timeoutMillis());
        statement.setInt(10, assignment.constraints().maxRetries());
    }

    private static AssignmentRow readAssignment(ResultSet result) throws SQLException {
        return new AssignmentRow(
                result.getString("assignment_id"),
                result.getString("execution_id"),
                result.getString("node_id"),
                result.getInt("attempt_number"),
                result.getString("assigned_worker_id"),
                result.getLong("assignment_fence_generation"),
                result.getString("assignment_status"),
                result.getLong("timeout_millis"),
                result.getInt("max_retries"));
    }

    private record AssignmentRow(String id, String executionId, String nodeId, int attempt,
                                 String assignedWorkerId, long fenceGeneration, String status,
                                 long timeoutMillis, int maxRetries) {
        private ExecutionAssignment toAssignment() {
            AssignmentId assignmentId = AssignmentId.of(id);
            return new DefaultExecutionAssignment(
                    assignmentId,
                    tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId),
                    tech.kayys.andalus.workflow.graph.NodeId.of(nodeId),
                    AttemptId.of(attempt),
                    WorkerId.of(assignedWorkerId),
                    AssignmentFence.of(assignmentId, fenceGeneration),
                    AssignmentStatus.valueOf(status),
                    new AssignmentConstraints(timeoutMillis, maxRetries));
        }
    }

    private static long positiveMillis(Duration duration, String name) {
        Objects.requireNonNull(duration, name + " cannot be null");
        if (duration.isZero() || duration.isNegative() || duration.toMillis() < 1) {
            throw new IllegalArgumentException(name + " must be at least one millisecond");
        }
        return duration.toMillis();
    }

    private static long nonNegativeMillis(Duration duration, String name) {
        Objects.requireNonNull(duration, name + " cannot be null");
        if (duration.isNegative()) throw new IllegalArgumentException(name + " cannot be negative");
        return duration.toMillis();
    }

    private static void rollback(Connection connection, Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            original.addSuppressed(rollbackFailure);
        }
    }

    private static IllegalStateException storageFailure(String message, SQLException cause) {
        return new IllegalStateException(message, cause);
    }
}
