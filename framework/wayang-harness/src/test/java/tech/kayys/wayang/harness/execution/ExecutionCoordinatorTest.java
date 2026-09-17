package tech.kayys.wayang.harness.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.context.DefaultHarnessContext;
import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.context.DefaultHarnessSession;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointId;
import tech.kayys.wayang.harness.execution.recovery.RecoveryAction;
import tech.kayys.wayang.harness.execution.recovery.RecoveryDecision;
import tech.kayys.wayang.harness.execution.state.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionCoordinatorTest {

    private DefaultExecutionCoordinator coordinator;
    private ExecutionId executionId;
    private DefaultExecutionState state;

    @BeforeEach
    void setUp() {
        coordinator = new DefaultExecutionCoordinator();
        executionId = ExecutionId.of("exec-coord-1");
        DefaultHarnessContext context = new DefaultHarnessContext(DefaultHarnessIdentity.of("agent-coord"), DefaultHarnessSession.createNew(), Map.of());
        state = new DefaultExecutionState(executionId, ExecutionStatus.RUNNING, 0L, context, ExecutionCursor.initial(), ExecutionData.empty());
    }

    @Test
    void testCheckpointAndRestore() {
        CheckpointId cpId = coordinator.checkpoint(state);
        assertNotNull(cpId);

        Optional<ExecutionState> restored = coordinator.restore(executionId);
        assertTrue(restored.isPresent());
        assertEquals(state.executionId(), restored.get().executionId());
        assertEquals(0L, restored.get().version());
    }

    @Test
    void testRecoverSuspendedExecution() {
        DefaultExecutionState suspended = state.withStatus(ExecutionStatus.SUSPENDED);
        coordinator.checkpoint(suspended);

        RecoveryDecision decision = coordinator.recover(executionId, null);
        assertEquals(RecoveryAction.RESUME, decision.action());
        assertTrue(decision.reason().contains("safely suspended"));
    }

    @Test
    void testRecoverTransientFailure() {
        coordinator.checkpoint(state);

        Throwable transientError = new IOException("Connection timeout to provider");
        RecoveryDecision decision = coordinator.recover(executionId, transientError);
        assertEquals(RecoveryAction.RETRY, decision.action());
        assertTrue(decision.reason().contains("Transient failure"));
    }
}
