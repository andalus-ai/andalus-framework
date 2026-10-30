package tech.kayys.andalus.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.ExecutionLifecycle;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionLifecycleTransitionsTest {

    @Test
    void allowsDocumentedAuthorizationAndExecutionProgression() {
        assertTrue(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.CREATED, ExecutionLifecycle.AUTHORIZING));
        assertTrue(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.AUTHORIZING, ExecutionLifecycle.AUTHORIZED));
        assertTrue(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.AUTHORIZED, ExecutionLifecycle.WAITING_OBLIGATION));
        assertTrue(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.WAITING_OBLIGATION, ExecutionLifecycle.RUNNING));
    }

    @Test
    void allowsSuspensionAndResumeProgression() {
        assertTrue(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.RUNNING, ExecutionLifecycle.SUSPENDED));
        assertTrue(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.SUSPENDED, ExecutionLifecycle.RESUMING));
        assertTrue(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.RESUMING, ExecutionLifecycle.RUNNING));
    }

    @Test
    void allowsTerminalOutcomesFromActiveStates() {
        assertTrue(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.RUNNING, ExecutionLifecycle.COMPLETED));
        assertTrue(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.RUNNING, ExecutionLifecycle.FAILED));
        assertTrue(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.RUNNING, ExecutionLifecycle.CANCELLED));
    }

    @Test
    void rejectsSkippingRequiredLifecycleStages() {
        assertFalse(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.CREATED, ExecutionLifecycle.RUNNING));
        assertFalse(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.AUTHORIZING, ExecutionLifecycle.COMPLETED));
        assertFalse(ExecutionLifecycleTransitions.isAllowed(
                ExecutionLifecycle.SUSPENDED, ExecutionLifecycle.COMPLETED));
    }

    @Test
    void terminalStatesCannotTransitionAgain() {
        for (ExecutionLifecycle terminal : new ExecutionLifecycle[] {
                ExecutionLifecycle.COMPLETED,
                ExecutionLifecycle.FAILED,
                ExecutionLifecycle.CANCELLED
        }) {
            for (ExecutionLifecycle target : ExecutionLifecycle.values()) {
                assertEquals(terminal == target,
                        ExecutionLifecycleTransitions.isAllowed(terminal, target),
                        () -> terminal + " -> " + target);
            }
        }
    }

    @Test
    void sameStateTransitionIsIdempotent() {
        for (ExecutionLifecycle state : ExecutionLifecycle.values()) {
            assertTrue(ExecutionLifecycleTransitions.isAllowed(state, state));
        }
    }
}
