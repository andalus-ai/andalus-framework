package tech.kayys.andalus.execution.control;

import java.util.Map;

/**
 * Validates whether a control action may transition the execution state machine.
 */
public interface ExecutionTransitionPolicy {

    TransitionDecision evaluate(ExecutionLifecycleState from, ControlAction action, Map<String, Object> context);
}
