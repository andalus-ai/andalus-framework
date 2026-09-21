package tech.kayys.andalus.harness.consistency.state;

/**
 * Validates and coordinates execution state machine transitions.
 */
public interface ExecutionStateMachine {

    boolean isAllowed(ExecutionState from, ExecutionState to);

    TransitionResult transition(ExecutionRecord current, ExecutionState target, TransitionContext context);
}
