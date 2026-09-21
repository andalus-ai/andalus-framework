package tech.kayys.andalus.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.core.lifecycle.DefaultExecutionStateMachine;
import tech.kayys.andalus.execution.lifecycle.ExecutionState;
import tech.kayys.andalus.execution.lifecycle.ExecutionTransition;
import tech.kayys.andalus.execution.lifecycle.TransitionResult;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutionStateMachineTest {

    @Test
    void testValidLifecycleProgression() {
        DefaultExecutionStateMachine sm = new DefaultExecutionStateMachine();
        String execId = "exec-001";

        assertEquals(ExecutionState.CREATED, sm.state(execId));

        // CREATED -> ADMITTED
        TransitionResult r1 = sm.transition(execId, ExecutionTransition.of(ExecutionState.CREATED, ExecutionState.ADMITTED, "admission granted"));
        assertTrue(r1.success());
        assertEquals(ExecutionState.ADMITTED, r1.state());

        // ADMITTED -> STARTING
        TransitionResult r2 = sm.transition(execId, ExecutionTransition.of(ExecutionState.ADMITTED, ExecutionState.STARTING, "worker assigned"));
        assertTrue(r2.success());
        assertEquals(ExecutionState.STARTING, r2.state());

        // STARTING -> RUNNING
        TransitionResult r3 = sm.transition(execId, ExecutionTransition.of(ExecutionState.STARTING, ExecutionState.RUNNING, "process started"));
        assertTrue(r3.success());
        assertEquals(ExecutionState.RUNNING, r3.state());

        // RUNNING -> CHECKPOINTING
        TransitionResult r4 = sm.transition(execId, ExecutionTransition.of(ExecutionState.RUNNING, ExecutionState.CHECKPOINTING, "saving milestone"));
        assertTrue(r4.success());
        assertEquals(ExecutionState.CHECKPOINTING, r4.state());

        // CHECKPOINTING -> RUNNING
        TransitionResult r5 = sm.transition(execId, ExecutionTransition.of(ExecutionState.CHECKPOINTING, ExecutionState.RUNNING, "checkpoint saved"));
        assertTrue(r5.success());
        assertEquals(ExecutionState.RUNNING, r5.state());

        // RUNNING -> COMPLETED
        TransitionResult r6 = sm.transition(execId, ExecutionTransition.of(ExecutionState.RUNNING, ExecutionState.COMPLETED, "work finished"));
        assertTrue(r6.success());
        assertEquals(ExecutionState.COMPLETED, r6.state());
        assertTrue(r6.state().isTerminal());
    }

    @Test
    void testRejectionOfInvalidTransition() {
        DefaultExecutionStateMachine sm = new DefaultExecutionStateMachine();
        String execId = "exec-invalid";

        // Illegal jump: CREATED -> RUNNING directly without ADMITTED/STARTING
        TransitionResult invalid = sm.transition(execId, ExecutionTransition.of(ExecutionState.CREATED, ExecutionState.RUNNING, "skip steps"));
        assertFalse(invalid.success());
        assertEquals(ExecutionState.CREATED, invalid.state());
        assertTrue(invalid.errorMessage().isPresent());
    }

    @Test
    void testFailureAndRecoveryBranch() {
        DefaultExecutionStateMachine sm = new DefaultExecutionStateMachine();
        String execId = "exec-recover";

        sm.transition(execId, ExecutionTransition.of(ExecutionState.CREATED, ExecutionState.ADMITTED, "ok"));
        sm.transition(execId, ExecutionTransition.of(ExecutionState.ADMITTED, ExecutionState.STARTING, "ok"));
        sm.transition(execId, ExecutionTransition.of(ExecutionState.STARTING, ExecutionState.RUNNING, "ok"));

        // Worker crash
        TransitionResult crash = sm.transition(execId, ExecutionTransition.of(ExecutionState.RUNNING, ExecutionState.CRASHED, "sigkill"));
        assertTrue(crash.success());
        assertTrue(crash.state().isRecoverable());

        // CRASHED -> RECOVERABLE
        TransitionResult recoverable = sm.transition(execId, ExecutionTransition.of(ExecutionState.CRASHED, ExecutionState.RECOVERABLE, "recovery initiated"));
        assertTrue(recoverable.success());

        // RECOVERABLE -> RESUMING
        TransitionResult resuming = sm.transition(execId, ExecutionTransition.of(ExecutionState.RECOVERABLE, ExecutionState.RESUMING, "restoring checkpoint"));
        assertTrue(resuming.success());

        // RESUMING -> RUNNING
        TransitionResult resumed = sm.transition(execId, ExecutionTransition.of(ExecutionState.RESUMING, ExecutionState.RUNNING, "resumed"));
        assertTrue(resumed.success());
        assertEquals(ExecutionState.RUNNING, resumed.state());
    }
}
