package tech.kayys.andalus.execution.core.mobility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.postgresql.ds.PGSimpleDataSource;
import tech.kayys.andalus.execution.mobility.ExecutionLease;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
class JdbcFencedLeaseManagerPostgresTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    private DataSource dataSource;

    @BeforeEach
    void setUp() throws Exception {
        PGSimpleDataSource postgres = new PGSimpleDataSource();
        postgres.setURL(POSTGRES.getJdbcUrl());
        postgres.setUser(POSTGRES.getUsername());
        postgres.setPassword(POSTGRES.getPassword());
        dataSource = postgres;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS andalus_execution_lease");
            statement.execute("""
                    CREATE TABLE andalus_execution_lease (
                        execution_id VARCHAR(512) NOT NULL PRIMARY KEY,
                        generation BIGINT NOT NULL,
                        holder_id VARCHAR(512) NULL,
                        acquired_at_ms BIGINT NOT NULL,
                        expires_at_ms BIGINT NOT NULL
                    )
                    """);
        }
    }

    private JdbcFencedLeaseManager manager() {
        return new JdbcFencedLeaseManager(dataSource);
    }

    @Test
    void postgresSerializesConcurrentFirstAcquisitionAcrossManagers() throws Exception {
        JdbcFencedLeaseManager firstNode = manager();
        JdbcFencedLeaseManager secondNode = manager();
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        try (var executor = Executors.newFixedThreadPool(2)) {
            List<Future<?>> futures = new ArrayList<>();
            futures.add(executor.submit(() -> acquire(firstNode, "node-a", ready, start, successes, rejected)));
            futures.add(executor.submit(() -> acquire(secondNode, "node-b", ready, start, successes, rejected)));
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            start.countDown();
            for (Future<?> future : futures) future.get(30, TimeUnit.SECONDS);
        }

        assertEquals(1, successes.get());
        assertEquals(1, rejected.get());
        assertEquals(1L, manager().currentToken("postgres-race").orElseThrow().generation());
    }

    @Test
    void postgresRejectsStaleFencingTokenAfterSuccessorAcquiresLease() throws Exception {
        JdbcFencedLeaseManager firstNode = manager();
        JdbcFencedLeaseManager secondNode = manager();
        ExecutionLease first = firstNode.acquireLease("postgres-fence", "node-a", Duration.ofMillis(100));

        Thread.sleep(250);

        ExecutionLease successor = secondNode.acquireLease("postgres-fence", "node-b", Duration.ofSeconds(10));

        assertFalse(firstNode.renewLease("postgres-fence", "node-a", first.fencingToken(), Duration.ofMinutes(1)));
        assertFalse(firstNode.releaseLease("postgres-fence", "node-a", first.fencingToken()));
        assertEquals(successor.fencingToken(),
                secondNode.getLease("postgres-fence").orElseThrow().fencingToken());
    }

    @Test
    void postgresPreservesGenerationAfterFencedReleaseAndReacquisition() {
        JdbcFencedLeaseManager firstNode = manager();
        ExecutionLease first = firstNode.acquireLease("postgres-release", "node-a", Duration.ofSeconds(10));

        assertTrue(firstNode.releaseLease("postgres-release", "node-a", first.fencingToken()));

        ExecutionLease successor = manager().acquireLease("postgres-release", "node-b", Duration.ofSeconds(10));

        assertEquals(first.fencingToken().generation() + 1, successor.fencingToken().generation());
        assertTrue(manager().validateToken("postgres-release", successor.fencingToken()));
        assertFalse(manager().validateToken("postgres-release", first.fencingToken()));
    }

    private static void acquire(JdbcFencedLeaseManager manager, String holder, CountDownLatch ready,
                                CountDownLatch start, AtomicInteger successes, AtomicInteger rejected) {
        ready.countDown();
        try {
            if (!start.await(10, TimeUnit.SECONDS)) throw new AssertionError("start signal timed out");
            manager.acquireLease("postgres-race", holder, Duration.ofMinutes(1));
            successes.incrementAndGet();
        } catch (IllegalStateException expected) {
            rejected.incrementAndGet();
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AssertionError(interrupted);
        }
    }
}
