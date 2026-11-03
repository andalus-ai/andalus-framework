package tech.kayys.andalus.execution.core.mobility;

import java.sql.Connection;
import java.sql.SQLException;

/** Supplies lease time from the authoritative lease database. */
@FunctionalInterface
public interface JdbcLeaseTimeSource {
    long currentTimeMillis(Connection connection) throws SQLException;
}
