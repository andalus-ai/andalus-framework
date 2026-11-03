package tech.kayys.andalus.execution.core.mobility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Database-specific lease clock expressions. */
public final class JdbcLeaseTimeSources {
    private JdbcLeaseTimeSources() { }

    /** Uses the standard SQL CURRENT_TIMESTAMP expression. */
    public static JdbcLeaseTimeSource standard() {
        return query("SELECT CURRENT_TIMESTAMP");
    }

    /** Uses PostgreSQL wall-clock time, not transaction-start CURRENT_TIMESTAMP. */
    public static JdbcLeaseTimeSource postgresql() {
        return query("SELECT clock_timestamp()");
    }

    private static JdbcLeaseTimeSource query(String sql) {
        return (Connection connection) -> {
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {
                if (!result.next()) throw new SQLException("Database lease clock returned no row");
                Timestamp timestamp = result.getTimestamp(1);
                if (timestamp == null) throw new SQLException("Database lease clock returned NULL");
                return timestamp.toInstant().toEpochMilli();
            }
        };
    }
}
