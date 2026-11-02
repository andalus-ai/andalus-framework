package tech.kayys.andalus.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.attempt.AttemptState;
import tech.kayys.andalus.execution.core.attempt.InMemoryExecutionAttemptStore;
import tech.kayys.andalus.execution.checkpoint.CheckpointIntegrity;
import tech.kayys.andalus.execution.checkpoint.CheckpointManifest;
import tech.kayys.andalus.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.andalus.execution.core.checkpoint.InMemoryExecutionCheckpointStore;
import tech.kayys.andalus.execution.core.mobility.DefaultLeaseManager;
import tech.kayys.andalus.execution.core.mobility.InMemoryExecutionStateStore;
import tech.kayys.andalus.execution.core.recovery.DefaultRecoveryManager;
import tech.kayys.andalus.execution.mobility.ExecutionCursor;
import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.mobility.ExecutionStatus;
import tech.kayys.andalus.execution.recovery.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CheckpointRestorationTest {

    @Test
    void restoresCheckpointBeforeMarkingAttemptAndExecutionRunning() {
        Fixture fixture = new Fixture("restore-success");
        fixture.saveCheckpoint();
        CheckpointRestorer restorer = request -> {
            assertEquals("checkpoint-1", request.checkpoint().checkpointId());
            assertEquals(ExecutionStatus.RESUMING, request.durableState().status());
            assertEquals("recovery-worker", request.targetWorkerId());
            return new CheckpointRestorationResult(
                    ExecutionCursor.of("workflow", "node-after-checkpoint", 2, "RUNNING"),
                    "sha256:restored-state");
        };
        DefaultRecoveryManager manager = fixture.manager(restorer);

        RecoveryResult result = manager.recover(manager.plan(fixture.executionId,
                manager.assess(fixture.executionId)));

        assertTrue(result.success());
        var attempt = fixture.attempts.get(AttemptId.of(result.newAttemptId().orElseThrow())).orElseThrow();
        assertEquals(AttemptState.RUNNING, attempt.state());
        var state = fixture.states.load(fixture.executionId).orElseThrow();
        assertEquals(ExecutionStatus.RUNNING, state.status());
        assertEquals("node-after-checkpoint", state.cursor().nodeId());
        assertEquals("sha256:restored-state", state.versionDigest());
    }

    @Test
    void failedRestorationLeavesAttemptFailedAndExecutionSuspended() {
        Fixture fixture = new Fixture("restore-failure");
        fixture.saveCheckpoint();
        DefaultRecoveryManager manager = fixture.manager(request -> {
            throw new IllegalStateException("decoder failed");
        });

        RecoveryResult result = manager.recover(manager.plan(fixture.executionId,
                manager.assess(fixture.executionId)));

        assertFalse(result.success());
        assertEquals(ExecutionStatus.SUSPENDED,
                fixture.states.load(fixture.executionId).orElseThrow().status());
        var attempt = fixture.attempts.getLatest(fixture.executionId).orElseThrow();
        assertEquals(AttemptState.FAILED, attempt.state());
    }

    @Test
    void doesNotClaimRuntimeIsRunningWhenNoRestorerIsConfigured() {
        Fixture fixture = new Fixture("restore-not-configured");
        fixture.saveCheckpoint();
        DefaultRecoveryManager manager = new DefaultRecoveryManager(fixture.checkpoints, fixture.leases,
                fixture.states, fixture.attempts, "recovery-worker", Duration.ofMinutes(2));

        RecoveryResult result = manager.recover(manager.plan(fixture.executionId,
                manager.assess(fixture.executionId)));

        assertTrue(result.success());
        assertEquals(ExecutionStatus.RECOVERING,
                fixture.states.load(fixture.executionId).orElseThrow().status());
        assertEquals(AttemptState.INITIALIZING,
                fixture.attempts.getLatest(fixture.executionId).orElseThrow().state());
    }

    private static final class Fixture {
        private final String executionId;
        private final InMemoryExecutionCheckpointStore checkpoints = new InMemoryExecutionCheckpointStore();
        private final DefaultLeaseManager leases = new DefaultLeaseManager();
        private final InMemoryExecutionStateStore states = new InMemoryExecutionStateStore(leases);
        private final InMemoryExecutionAttemptStore attempts = new InMemoryExecutionAttemptStore(leases);

        private Fixture(String executionId) {
            this.executionId = executionId;
            var initialLease = leases.acquireLease(executionId, "initial-worker", Duration.ofMinutes(1));
            states.save(ExecutionDurableState.of(
                    ExecutionIdentity.of(executionId, "workflow", "agent"),
                    ExecutionCursor.initial("workflow")), initialLease.fencingToken());
            leases.releaseLease(executionId, "initial-worker");
        }

        private void saveCheckpoint() {
            byte[] payload = "checkpoint-payload".getBytes(StandardCharsets.UTF_8);
            CheckpointManifest manifest = new CheckpointManifest("checkpoint-1", 1L,
                    List.of(), List.of(), List.of(), CheckpointIntegrity.sha256(payload), Instant.now(), Map.of());
            checkpoints.save(new ExecutionCheckpoint("checkpoint-1", executionId, AttemptId.random(),
                    1L, payload, manifest, Instant.now(), Map.of()));
        }

        private DefaultRecoveryManager manager(CheckpointRestorer restorer) {
            return new DefaultRecoveryManager(checkpoints, leases, states, attempts, restorer,
                    "recovery-worker", Duration.ofMinutes(2));
        }
    }
}
