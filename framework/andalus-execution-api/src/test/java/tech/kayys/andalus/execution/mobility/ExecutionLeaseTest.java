package tech.kayys.andalus.execution.mobility;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class ExecutionLeaseTest {

    @Test
    void expiryCanBeEvaluatedAgainstAnExplicitInstant() {
        Instant expiresAt = Instant.parse("2030-01-01T00:00:00Z");
        ExecutionLease lease = lease(expiresAt);

        assertFalse(lease.isExpired(expiresAt.minusNanos(1)));
        assertTrue(lease.isExpired(expiresAt));
        assertTrue(lease.isExpired(expiresAt.plusNanos(1)));
    }

    @Test
    void validityCanBeEvaluatedAgainstAnExplicitInstant() {
        Instant expiresAt = Instant.parse("2030-01-01T00:00:00Z");
        ExecutionLease lease = lease(expiresAt);

        assertTrue(lease.isValidFor("worker-a", lease.fencingToken(), expiresAt.minusNanos(1)));
        assertFalse(lease.isValidFor("worker-b", lease.fencingToken(), expiresAt.minusNanos(1)));
        assertFalse(lease.isValidFor("worker-a", lease.fencingToken(), expiresAt));
        assertFalse(lease.isValidFor("worker-a", lease.fencingToken(), expiresAt.plusNanos(1)));
    }

    @Test
    void explicitTimeMustNotBeNull() {
        ExecutionLease lease = lease(Instant.parse("2030-01-01T00:00:00Z"));

        assertThrows(NullPointerException.class, () -> lease.isExpired(null));
        assertThrows(NullPointerException.class,
                () -> lease.isValidFor("worker-a", lease.fencingToken(), null));
    }

    private static ExecutionLease lease(Instant expiresAt) {
        return new ExecutionLease("execution-1", "worker-a",
                new FencingToken(1L), Instant.parse("2029-12-31T23:59:00Z"), expiresAt);
    }
}
