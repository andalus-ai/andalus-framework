package tech.kayys.wayang.harness.consistency;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.consistency.checkpoint.CheckpointId;
import tech.kayys.wayang.harness.consistency.checkpoint.CheckpointManifest;
import tech.kayys.wayang.harness.consistency.checkpoint.DefaultCheckpoint;
import tech.kayys.wayang.harness.consistency.recovery.DefaultRecoveryPolicy;
import tech.kayys.wayang.harness.consistency.recovery.RecoveryDecision;
import tech.kayys.wayang.harness.consistency.recovery.RecoveryPolicy;
import tech.kayys.wayang.harness.consistency.state.*;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConsistencyTest {

    @Test
    void testExecutionStateMachineValidPath() {
        ExecutionStateMachine sm = new DefaultExecutionStateMachine();
        DefaultExecutionRecord record = DefaultExecutionRecord.create(ExecutionId.of("exec-1"));
        assertEquals(ExecutionState.CREATED, record.state());
        assertEquals(1L, record.version());

        TransitionContext ctx = TransitionContext.of("test");

        // CREATED -> ADMITTED
        TransitionResult r1 = sm.transition(record, ExecutionState.ADMITTED, ctx);
        assertTrue(r1.success());
        assertEquals(ExecutionState.ADMITTED, r1.record().orElseThrow().state());
        assertEquals(2L, r1.record().orElseThrow().version());

        // ADMITTED -> INITIALIZING
        TransitionResult r2 = sm.transition(r1.record().orElseThrow(), ExecutionState.INITIALIZING, ctx);
        assertTrue(r2.success());

        // INITIALIZING -> READY
        TransitionResult r3 = sm.transition(r2.record().orElseThrow(), ExecutionState.READY, ctx);
        assertTrue(r3.success());

        // READY -> RUNNING
        TransitionResult r4 = sm.transition(r3.record().orElseThrow(), ExecutionState.RUNNING, ctx);
        assertTrue(r4.success());

        // RUNNING -> COMPLETING
        TransitionResult r5 = sm.transition(r4.record().orElseThrow(), ExecutionState.COMPLETING, ctx);
        assertTrue(r5.success());

        // COMPLETING -> COMMITTED
        TransitionResult r6 = sm.transition(r5.record().orElseThrow(), ExecutionState.COMMITTED, ctx);
        assertTrue(r6.success());
        assertTrue(r6.record().orElseThrow().state().isTerminal());
    }

    @Test
    void testExecutionStateMachineInvalidTransitions() {
        ExecutionStateMachine sm = new DefaultExecutionStateMachine();
        DefaultExecutionRecord record = DefaultExecutionRecord.create(ExecutionId.of("exec-2"));

        TransitionContext ctx = TransitionContext.of("test");

        // Cannot jump directly from CREATED to RUNNING
        TransitionResult invalid = sm.transition(record, ExecutionState.RUNNING, ctx);
        assertFalse(invalid.success());

        // Cannot transition out of terminal COMMITTED
        DefaultExecutionRecord committed = record.withState(ExecutionState.COMMITTED);
        TransitionResult fromCommitted = sm.transition(committed, ExecutionState.RUNNING, ctx);
        assertFalse(fromCommitted.success());
    }

    @Test
    void testCheckpointAndRecoveryPolicy() {
        ExecutionId execId = ExecutionId.of("exec-3");
        CheckpointId cpId = CheckpointId.generate();

        CheckpointManifest manifest = CheckpointManifest.of(
                Map.of("step", 3),
                Set.of("artifact-1"),
                Map.of("repo", "my-repo"),
                1L
        );

        DefaultCheckpoint checkpoint = DefaultCheckpoint.of(cpId, execId, 1L, manifest);
        assertEquals(cpId, checkpoint.id());
        assertEquals(1L, checkpoint.sequence());

        DefaultExecutionRecord record = DefaultExecutionRecord.create(execId).withCheckpoint(cpId);
        assertTrue(record.checkpoint().isPresent());

        RecoveryPolicy policy = new DefaultRecoveryPolicy();

        // Transient error -> RETRY
        RecoveryDecision d1 = policy.decide(FailureInfo.transientError("Network timeout"), record);
        assertEquals(RecoveryDecision.RETRY, d1);

        // Non-transient with checkpoint -> RESUME
        FailureInfo nonTransient = FailureInfo.of(FailureClass.TOOL_FAILURE, "TOOL_ERR", "Tool failed", false, true);
        RecoveryDecision d2 = policy.decide(nonTransient, record);
        assertEquals(RecoveryDecision.RESUME, d2);

        // Terminal error -> FAIL
        RecoveryDecision d3 = policy.decide(FailureInfo.terminal("Corrupted storage"), DefaultExecutionRecord.create(execId));
        assertEquals(RecoveryDecision.FAIL, d3);
    }
}
