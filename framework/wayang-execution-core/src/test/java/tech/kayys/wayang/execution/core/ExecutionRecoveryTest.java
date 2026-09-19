package tech.kayys.wayang.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.attempt.AttemptId;
import tech.kayys.wayang.execution.checkpoint.CheckpointIntegrity;
import tech.kayys.wayang.execution.checkpoint.CheckpointManifest;
import tech.kayys.wayang.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.wayang.execution.core.checkpoint.InMemoryExecutionCheckpointStore;
import tech.kayys.wayang.execution.core.recovery.DefaultExecutionRecoveryCoordinator;
import tech.kayys.wayang.execution.core.recovery.DefaultRecoveryManager;
import tech.kayys.wayang.execution.recovery.*;

import java.nio.charset.StandardCharsets;
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
}
