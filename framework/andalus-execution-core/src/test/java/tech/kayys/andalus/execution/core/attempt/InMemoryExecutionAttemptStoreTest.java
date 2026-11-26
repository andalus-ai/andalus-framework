package tech.kayys.andalus.execution.core.attempt;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.attempt.AttemptLineage;
import tech.kayys.andalus.execution.attempt.ExecutionAttempt;
import tech.kayys.andalus.execution.core.mobility.DefaultLeaseManager;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryExecutionAttemptStoreTest {

    @Test
    void persistsSequentialAttemptLineageUnderActiveFence() {
        DefaultLeaseManager leases = new DefaultLeaseManager();
        InMemoryExecutionAttemptStore store = new InMemoryExecutionAttemptStore(leases);
        var lease = leases.acquireLease("exec-attempts", "worker", Duration.ofMinutes(1));

        ExecutionAttempt first = ExecutionAttempt.initial("exec-attempts", "worker");
        AttemptLineage firstLineage = new AttemptLineage("exec-attempts", first.attemptId(),
                Optional.empty(), Optional.empty());
        store.save(first, firstLineage, lease.fencingToken());

        ExecutionAttempt second = first.next("worker");
        AttemptLineage secondLineage = new AttemptLineage("exec-attempts", second.attemptId(),
                Optional.of(first.attemptId()), Optional.of("cp-1"));
        store.save(second, secondLineage, lease.fencingToken());

        assertEquals(2, store.list("exec-attempts").size());
        assertEquals(second.attemptId(), store.getLatest("exec-attempts").orElseThrow().attemptId());
        assertEquals(first.attemptId(), store.getLineage(second.attemptId()).orElseThrow()
                .parentAttemptId().orElseThrow());
        assertEquals("cp-1", store.getLineage(second.attemptId()).orElseThrow()
                .restoredFromCheckpointId().orElseThrow());
    }

    @Test
    void rejectsStaleFenceAndDuplicateAttemptId() {
        DefaultLeaseManager leases = new DefaultLeaseManager();
        InMemoryExecutionAttemptStore store = new InMemoryExecutionAttemptStore(leases);
        var oldLease = leases.acquireLease("exec-fenced", "worker-a", Duration.ofMinutes(1));
        ExecutionAttempt attempt = ExecutionAttempt.initial("exec-fenced", "worker-a");
        AttemptLineage lineage = new AttemptLineage("exec-fenced", attempt.attemptId(),
                Optional.empty(), Optional.empty());
        leases.releaseLease("exec-fenced", "worker-a");

        assertThrows(IllegalStateException.class,
                () -> store.save(attempt, lineage, oldLease.fencingToken()));

        var newLease = leases.acquireLease("exec-fenced", "worker-b", Duration.ofMinutes(1));
        store.save(attempt, lineage, newLease.fencingToken());
        assertThrows(IllegalStateException.class,
                () -> store.save(attempt, lineage, newLease.fencingToken()));
    }

    @Test
    void rejectsNonSequentialAttemptNumbers() {
        DefaultLeaseManager leases = new DefaultLeaseManager();
        InMemoryExecutionAttemptStore store = new InMemoryExecutionAttemptStore(leases);
        var lease = leases.acquireLease("exec-order", "worker", Duration.ofMinutes(1));
        ExecutionAttempt skipped = new ExecutionAttempt("exec-order", AttemptId.random(), 2,
                tech.kayys.andalus.execution.attempt.AttemptState.INITIALIZING, "worker",
                java.time.Instant.now(), Optional.empty());
        AttemptLineage lineage = new AttemptLineage("exec-order", skipped.attemptId(),
                Optional.empty(), Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> store.save(skipped, lineage, lease.fencingToken()));
    }
}
