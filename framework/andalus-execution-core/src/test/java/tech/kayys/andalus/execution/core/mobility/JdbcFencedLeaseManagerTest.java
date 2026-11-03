package tech.kayys.andalus.execution.core.mobility;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.mobility.ExecutionLease;

import java.sql.Connection;
import java.sql.Statement;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class JdbcFencedLeaseManagerTest {
    private JdbcDataSource dataSource;
    private MutableClock clock;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:lease_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000");
        dataSource.setUser("sa");
        clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
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
        return new JdbcFencedLeaseManager(dataSource, connection -> clock.millis());
    }

    @Test
    void generationSurvivesReleaseAndNewManagerInstance() {
        ExecutionLease first = manager().acquireLease("exec-1", "node-a", Duration.ofSeconds(10));
        assertTrue(manager().releaseLease("exec-1", "node-a", first.fencingToken()));

        ExecutionLease second = manager().acquireLease("exec-1", "node-b", Duration.ofSeconds(10));

        assertEquals(first.fencingToken().generation() + 1, second.fencingToken().generation());
        assertFalse(manager().validateToken("exec-1", first.fencingToken()));
        assertTrue(manager().validateToken("exec-1", second.fencingToken()));
    }

    @Test
    void staleHolderCannotRenewOrReleaseSuccessor() {
        JdbcFencedLeaseManager firstNode = manager();
        JdbcFencedLeaseManager secondNode = manager();
        ExecutionLease first = firstNode.acquireLease("exec-2", "node-a", Duration.ofSeconds(5));
        clock.advance(Duration.ofSeconds(6));
        ExecutionLease successor = secondNode.acquireLease("exec-2", "node-b", Duration.ofSeconds(10));

        assertFalse(firstNode.renewLease("exec-2", "node-a", first.fencingToken(), Duration.ofMinutes(1)));
        assertFalse(firstNode.releaseLease("exec-2", "node-a", first.fencingToken()));
        assertEquals(successor.fencingToken(), secondNode.getLease("exec-2").orElseThrow().fencingToken());
    }

    @Test
    void fencedRenewalRetainsGenerationAndExtendsExpiry() {
        ExecutionLease lease = manager().acquireLease("exec-3", "node-a", Duration.ofSeconds(10));
        clock.advance(Duration.ofSeconds(2));

        assertTrue(manager().renewLease("exec-3", "node-a", lease.fencingToken(), Duration.ofSeconds(20)));

        ExecutionLease renewed = manager().getLease("exec-3").orElseThrow();
        assertEquals(lease.fencingToken(), renewed.fencingToken());
        assertEquals(clock.instant().plusSeconds(20), renewed.expiresAt());
    }

    @Test
    void expiredLeaseIsNotVisibleAndCanBeReacquiredWithNewGeneration() {
        ExecutionLease lease = manager().acquireLease("exec-4", "node-a", Duration.ofSeconds(1));
        clock.advance(Duration.ofSeconds(1));

        assertTrue(manager().getLease("exec-4").isEmpty());
        ExecutionLease successor = manager().acquireLease("exec-4", "node-b", Duration.ofSeconds(1));
        assertTrue(successor.fencingToken().isAfter(lease.fencingToken()));
    }

    @Test
    void rejectsDurationsThatOverflowMillisecondConversionOrExpiry() {
        assertThrows(IllegalArgumentException.class,
                () -> manager().acquireLease("exec-overflow", "node-a", Duration.ofSeconds(Long.MAX_VALUE)));

        clock.set(Instant.ofEpochMilli(Long.MAX_VALUE - 5));
        assertThrows(IllegalArgumentException.class,
                () -> manager().acquireLease("exec-overflow", "node-a", Duration.ofMillis(10)));
        assertTrue(manager().currentToken("exec-overflow").isEmpty());
    }

    @Test
    void generationOverflowFailsWithoutChangingTheCounter() throws Exception {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    INSERT INTO andalus_execution_lease
                        (execution_id,generation,holder_id,acquired_at_ms,expires_at_ms)
                    VALUES ('exec-generation-max',9223372036854775807,NULL,0,0)
                    """);
        }

        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> manager().acquireLease("exec-generation-max", "node-a", Duration.ofSeconds(10)));

        assertTrue(failure.getMessage().contains("Fencing generation exhausted"));
        assertEquals(Long.MAX_VALUE,
                manager().currentToken("exec-generation-max").orElseThrow().generation());
        assertTrue(manager().getLease("exec-generation-max").isEmpty());
    }

    @Test
    void concurrentFirstAcquisitionHasSingleWinnerAcrossManagerInstances() throws Exception {
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
            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            for (Future<?> future : futures) future.get(10, TimeUnit.SECONDS);
        }

        assertEquals(1, successes.get());
        assertEquals(1, rejected.get());
        assertEquals(1L, manager().currentToken("exec-race").orElseThrow().generation());
    }

    private static void acquire(JdbcFencedLeaseManager manager, String holder, CountDownLatch ready,
                                CountDownLatch start, AtomicInteger successes, AtomicInteger rejected) {
        ready.countDown();
        try {
            if (!start.await(5, TimeUnit.SECONDS)) throw new AssertionError("start signal timed out");
            manager.acquireLease("exec-race", holder, Duration.ofMinutes(1));
            successes.incrementAndGet();
        } catch (IllegalStateException expected) {
            rejected.incrementAndGet();
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AssertionError(interrupted);
        }
    }

    private static final class MutableClock extends Clock {
        private Instant now;
        private MutableClock(Instant now) { this.now = now; }
        void advance(Duration duration) { now = now.plus(duration); }
        void set(Instant instant) { now = instant; }
        @Override public ZoneOffset getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(java.time.ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }
}
