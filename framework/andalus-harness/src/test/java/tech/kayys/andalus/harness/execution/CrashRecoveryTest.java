package tech.kayys.andalus.harness.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.execution.action.ActionExecutionMode;
import tech.kayys.andalus.harness.execution.action.ActionId;
import tech.kayys.andalus.harness.execution.action.ActionJournal;
import tech.kayys.andalus.harness.execution.action.ActionRecord;
import tech.kayys.andalus.harness.execution.action.InMemoryActionJournal;
import tech.kayys.andalus.harness.execution.recovery.CrashRecoveryAnalyzer;
import tech.kayys.andalus.harness.execution.recovery.RecoveryAction;
import tech.kayys.andalus.harness.execution.recovery.RecoveryDecision;
import tech.kayys.andalus.harness.execution.state.DefaultExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionCursor;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the 6.13 crash recovery model:
 * - STARTED action after crash = UNKNOWN, not FAILED
 * - Idempotent actions → RETRY
 * - AT_MOST_ONCE / NON_REPLAYABLE → WAIT (reconcile required)
 * - No in-flight actions → RESUME
 * - DefaultExecutionCoordinator passes action history to recovery context
 */
class CrashRecoveryTest {

    private CrashRecoveryAnalyzer analyzer;
    private ExecutionId executionId;

    @BeforeEach
    void setUp() {
        analyzer = new CrashRecoveryAnalyzer();
        executionId = ExecutionId.of("exec-crash-001");
    }

    @Test
    void noActions_shouldResume() {
        RecoveryDecision decision = analyzer.analyze(List.of());
        assertEquals(RecoveryAction.RESUME, decision.action(),
                "No actions before crash → safe to resume");
    }

    @Test
    void allActionsCompleted_shouldResume() {
        ActionRecord completed = ActionRecord.started(ActionId.generate(), executionId, "order-lookup", ActionExecutionMode.IDEMPOTENT)
                .complete("result");
        RecoveryDecision decision = analyzer.analyze(List.of(completed));
        assertEquals(RecoveryAction.RESUME, decision.action(),
                "All COMPLETED actions → no in-flight → resume");
    }

    @Test
    void startedIdempotentAction_shouldRetry() {
        // IDEMPOTENT STARTED = safe to replay
        ActionRecord inFlight = ActionRecord.started(ActionId.generate(), executionId, "create-order",
                ActionExecutionMode.IDEMPOTENT);
        RecoveryDecision decision = analyzer.analyze(List.of(inFlight));
        assertEquals(RecoveryAction.RETRY, decision.action(),
                "STARTED idempotent action after crash → outcome UNKNOWN → replay safe → RETRY");
        assertFalse(decision.reason().isBlank());
    }

    @Test
    void startedPureAction_shouldRetry() {
        ActionRecord inFlight = ActionRecord.started(ActionId.generate(), executionId, "compute",
                ActionExecutionMode.PURE);
        RecoveryDecision decision = analyzer.analyze(List.of(inFlight));
        assertEquals(RecoveryAction.RETRY, decision.action());
    }

    @Test
    void startedAtMostOnceAction_shouldWaitForReconciliation() {
        // AT_MOST_ONCE = cannot safely replay → must confirm
        ActionRecord inFlight = ActionRecord.started(ActionId.generate(), executionId, "send-payment",
                ActionExecutionMode.AT_MOST_ONCE);
        RecoveryDecision decision = analyzer.analyze(List.of(inFlight));
        assertEquals(RecoveryAction.WAIT, decision.action(),
                "STARTED AT_MOST_ONCE action after crash → outcome UNKNOWN → reconcile required → WAIT");
    }

    @Test
    void startedNonReplayableAction_shouldWaitForReconciliation() {
        ActionRecord inFlight = ActionRecord.started(ActionId.generate(), executionId, "irreversible-op",
                ActionExecutionMode.NON_REPLAYABLE);
        RecoveryDecision decision = analyzer.analyze(List.of(inFlight));
        assertEquals(RecoveryAction.WAIT, decision.action(),
                "NON_REPLAYABLE → WAIT for operator confirmation");
    }

    @Test
    void mixedActions_mostConservativeWins() {
        // One idempotent (retry-safe) + one AT_MOST_ONCE (wait) → most conservative = WAIT
        ActionRecord idempotent = ActionRecord.started(ActionId.generate(), executionId, "read-cache",
                ActionExecutionMode.IDEMPOTENT);
        ActionRecord atMostOnce = ActionRecord.started(ActionId.generate(), executionId, "debit-account",
                ActionExecutionMode.AT_MOST_ONCE);
        RecoveryDecision decision = analyzer.analyze(List.of(idempotent, atMostOnce));
        assertEquals(RecoveryAction.WAIT, decision.action(),
                "Mixed: most conservative action (AT_MOST_ONCE) wins → WAIT");
    }

    @Test
    void coordinator_passesActionHistoryToRecovery() {
        // Verify DefaultExecutionCoordinator enriches the context with action history
        DefaultExecutionCoordinator coordinator = new DefaultExecutionCoordinator();
        ExecutionId id = ExecutionId.of("exec-ctx-check");

        // Save a RUNNING execution state
        DefaultExecutionState state = new DefaultExecutionState(
                id, ExecutionStatus.RUNNING, 1L, null, ExecutionCursor.initial(), null);
        coordinator.stateStore().save(state, 0L);

        // Record an in-flight IDEMPOTENT action
        ActionRecord inFlight = ActionRecord.started(ActionId.generate(), id, "process-order",
                ActionExecutionMode.IDEMPOTENT);
        coordinator.journal().started(inFlight);

        // Trigger recovery — should see RETRY because action is idempotent
        RecoveryDecision decision = coordinator.recover(id, new RuntimeException("connection reset"));
        assertEquals(RecoveryAction.RETRY, decision.action(),
                "Coordinator should pass action history into context; IDEMPOTENT STARTED → RETRY");
    }
}
