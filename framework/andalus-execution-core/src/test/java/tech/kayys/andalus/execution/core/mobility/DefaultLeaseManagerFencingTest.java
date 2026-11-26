package tech.kayys.andalus.execution.core.mobility;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.FencingToken;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class DefaultLeaseManagerFencingTest {

    @Test
    void releasedLeaseImmediatelyInvalidatesItsToken() {
        DefaultLeaseManager manager = new DefaultLeaseManager();
        ExecutionLease lease = manager.acquireLease("exec-1", "worker-a", Duration.ofMinutes(1));

        assertTrue(manager.validateToken("exec-1", lease.fencingToken()));
        manager.releaseLease("exec-1", "worker-a");

        assertFalse(manager.validateToken("exec-1", lease.fencingToken()));
        assertTrue(manager.getLease("exec-1").isEmpty());
    }

    @Test
    void expiredLeaseCannotAuthorizeWritesOrBeRenewed() throws InterruptedException {
        DefaultLeaseManager manager = new DefaultLeaseManager();
        ExecutionLease lease = manager.acquireLease("exec-2", "worker-a", Duration.ofMillis(1));
        Thread.sleep(10);

        assertFalse(manager.validateToken("exec-2", lease.fencingToken()));
        assertFalse(manager.renewLease("exec-2", "worker-a", Duration.ofMinutes(1)));
        assertTrue(manager.getLease("exec-2").isEmpty());
    }

    @Test
    void activeLeaseCannotBeStolenButCanBeReacquiredBySameHolderWithNewGeneration() {
        DefaultLeaseManager manager = new DefaultLeaseManager();
        ExecutionLease first = manager.acquireLease("exec-3", "worker-a", Duration.ofMinutes(1));

        assertThrows(IllegalStateException.class,
                () -> manager.acquireLease("exec-3", "worker-b", Duration.ofMinutes(1)));

        ExecutionLease renewedOwnership = manager.acquireLease("exec-3", "worker-a", Duration.ofMinutes(1));
        assertTrue(renewedOwnership.fencingToken().generation() > first.fencingToken().generation());
        assertFalse(manager.validateToken("exec-3", first.fencingToken()));
        assertTrue(manager.validateToken("exec-3", renewedOwnership.fencingToken()));
    }

    @Test
    void releaseFromNonOwnerDoesNotInvalidateActiveLease() {
        DefaultLeaseManager manager = new DefaultLeaseManager();
        ExecutionLease lease = manager.acquireLease("exec-4", "worker-a", Duration.ofMinutes(1));

        manager.releaseLease("exec-4", "worker-b");

        assertTrue(manager.validateToken("exec-4", lease.fencingToken()));
        assertEquals("worker-a", manager.getLease("exec-4").orElseThrow().holderId());
    }

    @Test
    void rejectsNonPositiveLeaseDurationsAndBlankIdentities() {
        DefaultLeaseManager manager = new DefaultLeaseManager();

        assertThrows(IllegalArgumentException.class,
                () -> manager.acquireLease("exec-5", "worker-a", Duration.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> manager.acquireLease(" ", "worker-a", Duration.ofSeconds(1)));
        assertThrows(IllegalArgumentException.class,
                () -> manager.acquireLease("exec-5", " ", Duration.ofSeconds(1)));
        assertFalse(manager.validateToken("exec-5", FencingToken.of(1)));
    }

    @Test
    void staleGenerationCannotRenewSuccessorLeaseEvenForSameHolder() {
        DefaultLeaseManager manager = new DefaultLeaseManager();
        ExecutionLease first = manager.acquireLease("tenant-a/agent-a", "node-a", Duration.ofMinutes(1));
        ExecutionLease successor = manager.acquireLease("tenant-a/agent-a", "node-a", Duration.ofMinutes(1));

        assertTrue(successor.fencingToken().isAfter(first.fencingToken()));
        assertFalse(manager.renewLease(first.executionId(), first.holderId(),
                first.fencingToken(), Duration.ofMinutes(2)));
        assertEquals(successor.fencingToken(),
                manager.getLease(successor.executionId()).orElseThrow().fencingToken());
    }

    @Test
    void staleGenerationCannotReleaseSuccessorLeaseEvenForSameHolder() {
        DefaultLeaseManager manager = new DefaultLeaseManager();
        ExecutionLease first = manager.acquireLease("tenant-a/agent-a", "node-a", Duration.ofMinutes(1));
        ExecutionLease successor = manager.acquireLease("tenant-a/agent-a", "node-a", Duration.ofMinutes(1));

        assertFalse(manager.releaseLease(first.executionId(), first.holderId(), first.fencingToken()));
        assertEquals(successor.fencingToken(),
                manager.getLease(successor.executionId()).orElseThrow().fencingToken());
    }

    @Test
    void currentGenerationCanRenewAndReleaseWithExactToken() {
        DefaultLeaseManager manager = new DefaultLeaseManager();
        ExecutionLease lease = manager.acquireLease("tenant-a/agent-a", "node-a", Duration.ofMinutes(1));

        assertTrue(manager.renewLease(lease.executionId(), lease.holderId(),
                lease.fencingToken(), Duration.ofMinutes(2)));
        assertEquals(lease.fencingToken(),
                manager.getLease(lease.executionId()).orElseThrow().fencingToken());
        assertTrue(manager.releaseLease(lease.executionId(), lease.holderId(), lease.fencingToken()));
        assertTrue(manager.getLease(lease.executionId()).isEmpty());
    }
}
