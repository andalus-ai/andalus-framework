package tech.kayys.andalus.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.checkpoint.CheckpointIntegrity;
import tech.kayys.andalus.execution.checkpoint.CheckpointManifest;
import tech.kayys.andalus.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.andalus.execution.core.checkpoint.InMemoryExecutionCheckpointStore;
import tech.kayys.andalus.execution.core.recovery.DefaultExecutionRecoveryCoordinator;
import tech.kayys.andalus.execution.core.recovery.DefaultRecoveryManager;
import tech.kayys.andalus.execution.recovery.*;
import tech.kayys.andalus.execution.mobility.ExecutionCursor;
import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.mobility.ExecutionStatus;
import tech.kayys.andalus.execution.core.mobility.DefaultLeaseManager;
import tech.kayys.andalus.execution.core.mobility.InMemoryExecutionStateStore;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutionRecoveryTest {

    @Test
    void testRecoveryAssessmentPlanAndExecution() {
        InMemoryExecutionCheckpointStore checkpointStore = new InMemoryExecutionCheckpointStore();
        DefaultRecoveryManager recoveryManager = new DefaultRecoveryManager(checkpointStore);
        DefaultExecutionRecoveryCoordinator coordinator = new DefaultExecutionRecoveryCoordinator(recoveryManager);

        String execId = "exec-recover-test";

        // 1. Without checkpoint -> assessment recommends RESTART
        RecoveryAssessment noCpAssessment = coordinator.assess(execId);
        assertTrue(noCpAssessment.recoverable());
        assertEquals(RecoveryMode.RESTART, noCpAssessment.recommendedMode());

        // 2. Add checkpoint
        byte[] payload = "saved-progress".getBytes(StandardCharsets.UTF_8);
        CheckpointManifest manifest = new CheckpointManifest(
                "cp-99", 1L, List.of(), List.of(), List.of(), CheckpointIntegrity.sha256(payload), Instant.now(), Map.of()
        );
        checkpointStore.save(new ExecutionCheckpoint("cp-99", execId, AttemptId.random(), 1L, payload, manifest, Instant.now(), Map.of()));

        // 3. With checkpoint -> assessment recommends RESUME
        RecoveryAssessment cpAssessment = coordinator.assess(execId);
        assertTrue(cpAssessment.recoverable());
        assertEquals(RecoveryMode.RESUME, cpAssessment.recommendedMode());
        assertTrue(cpAssessment.latestCheckpoint().isPresent());

        // 4. Plan
        RecoveryPlan plan = coordinator.plan(execId, cpAssessment);
        assertNotNull(plan);
        assertEquals(RecoveryMode.RESUME, plan.mode());
        assertEquals("cp-99", plan.checkpointId().orElseThrow());

        // 5. Execute recovery
        RecoveryResult result = coordinator.execute(plan);
        assertTrue(result.success());
        assertEquals(RecoveryMode.RESUME, result.modeUsed());
        assertTrue(result.newAttemptId().isPresent());
    }

    @Test
    void rejectsAssessmentForAnotherExecution() {
        InMemoryExecutionCheckpointStore checkpointStore = new InMemoryExecutionCheckpointStore();
        DefaultRecoveryManager manager = new DefaultRecoveryManager(checkpointStore);
        RecoveryAssessment assessment = manager.assess("execution-a");

        assertThrows(IllegalArgumentException.class, () -> manager.plan("execution-b", assessment));
    }

    @Test
    void rejectsStaleRestartPlanWhenCheckpointAppearsBeforeRecovery() {
        InMemoryExecutionCheckpointStore checkpointStore = new InMemoryExecutionCheckpointStore();
        DefaultRecoveryManager manager = new DefaultRecoveryManager(checkpointStore);
        RecoveryPlan stalePlan = manager.plan("execution-race", manager.assess("execution-race"));

        byte[] payload = "progress".getBytes(StandardCharsets.UTF_8);
        CheckpointManifest manifest = new CheckpointManifest(
                "cp-race", 1L, List.of(), List.of(), List.of(), CheckpointIntegrity.sha256(payload),
                Instant.now(), Map.of());
        checkpointStore.save(new ExecutionCheckpoint("cp-race", "execution-race", AttemptId.random(),
                1L, payload, manifest, Instant.now(), Map.of()));

        RecoveryResult result = manager.recover(stalePlan);
        assertFalse(result.success());
        assertTrue(result.errorMessage().orElseThrow().contains("checkpoint now exists"));
    }

    @Test
    void rejectsStaleResumePlanWhenNewerCheckpointAppears() {
        InMemoryExecutionCheckpointStore checkpointStore = new InMemoryExecutionCheckpointStore();
        DefaultRecoveryManager manager = new DefaultRecoveryManager(checkpointStore);
        saveCheckpoint(checkpointStore, "execution-resume-race", "cp-first", 1L);
        RecoveryPlan stalePlan = manager.plan("execution-resume-race",
                manager.assess("execution-resume-race"));
        saveCheckpoint(checkpointStore, "execution-resume-race", "cp-second", 2L);

        RecoveryResult result = manager.recover(stalePlan);
        assertFalse(result.success());
        assertTrue(result.errorMessage().orElseThrow().contains("missing or stale"));
    }

    @Test
    void rejectsStaleAssessmentDuringPlanning() {
        InMemoryExecutionCheckpointStore checkpointStore = new InMemoryExecutionCheckpointStore();
        DefaultRecoveryManager manager = new DefaultRecoveryManager(checkpointStore);
        RecoveryAssessment staleAssessment = manager.assess("execution-plan-race");
        saveCheckpoint(checkpointStore, "execution-plan-race", "cp-new", 1L);

        assertThrows(IllegalStateException.class,
                () -> manager.plan("execution-plan-race", staleAssessment));
    }

    private static void saveCheckpoint(InMemoryExecutionCheckpointStore store, String executionId,
            String checkpointId, long sequence) {
        byte[] payload = ("payload-" + sequence).getBytes(StandardCharsets.UTF_8);
        CheckpointManifest manifest = new CheckpointManifest(checkpointId, sequence, List.of(), List.of(),
                List.of(), CheckpointIntegrity.sha256(payload), Instant.now(), Map.of());
        store.save(new ExecutionCheckpoint(checkpointId, executionId, AttemptId.random(), sequence,
                payload, manifest, Instant.now(), Map.of()));
    }

    @Test
    void claimsFencedLeaseAndPersistsRecoveringBeforeReturningSuccess() {
        InMemoryExecutionCheckpointStore checkpoints = new InMemoryExecutionCheckpointStore();
        DefaultLeaseManager leases = new DefaultLeaseManager();
        InMemoryExecutionStateStore states = new InMemoryExecutionStateStore(leases);
        String executionId = "execution-fenced-recovery";
        ExecutionIdentity identity = ExecutionIdentity.of(executionId, "workflow", "agent");
        ExecutionDurableState initial = ExecutionDurableState.of(identity,
                ExecutionCursor.initial("workflow"));
        var previousLease = leases.acquireLease(executionId, "expired-worker", Duration.ofMillis(1));
        states.save(initial, previousLease.fencingToken());
        leases.releaseLease(executionId, "expired-worker");

        DefaultRecoveryManager manager = new DefaultRecoveryManager(checkpoints, leases, states,
                "recovery-worker", Duration.ofMinutes(2));
        RecoveryPlan plan = manager.plan(executionId, manager.assess(executionId));

        RecoveryResult result = manager.recover(plan);

        assertTrue(result.success());
        assertEquals(ExecutionStatus.RECOVERING, states.load(executionId).orElseThrow().status());
        assertEquals("recovery-worker", leases.getLease(executionId).orElseThrow().holderId());
        assertTrue(leases.validateToken(executionId,
                leases.getLease(executionId).orElseThrow().fencingToken()));
    }

    @Test
    void refusesRecoveryWhenAnotherWorkerStillOwnsTheLease() {
        InMemoryExecutionCheckpointStore checkpoints = new InMemoryExecutionCheckpointStore();
        DefaultLeaseManager leases = new DefaultLeaseManager();
        InMemoryExecutionStateStore states = new InMemoryExecutionStateStore(leases);
        String executionId = "execution-active-owner";
        ExecutionIdentity identity = ExecutionIdentity.of(executionId, "workflow", "agent");
        ExecutionDurableState initial = ExecutionDurableState.of(identity,
                ExecutionCursor.initial("workflow"));
        var activeLease = leases.acquireLease(executionId, "active-worker", Duration.ofMinutes(5));
        states.save(initial, activeLease.fencingToken());

        DefaultRecoveryManager manager = new DefaultRecoveryManager(checkpoints, leases, states,
                "recovery-worker", Duration.ofMinutes(2));
        RecoveryPlan plan = manager.plan(executionId, manager.assess(executionId));

        RecoveryResult result = manager.recover(plan);

        assertFalse(result.success());
        assertEquals("active-worker", leases.getLease(executionId).orElseThrow().holderId());
        assertEquals(ExecutionStatus.CREATED, states.load(executionId).orElseThrow().status());
    }
}
