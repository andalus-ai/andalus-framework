package tech.kayys.wayang.execution.core.control;

import tech.kayys.wayang.execution.control.ControlAction;
import tech.kayys.wayang.execution.control.ExecutionLifecycleState;
import tech.kayys.wayang.execution.control.ExecutionTransitionPolicy;
import tech.kayys.wayang.execution.control.TransitionDecision;

import java.util.Map;

/**
 * Standard implementation of {@link ExecutionTransitionPolicy} enforcing authoritative lifecycle state transitions.
 */
public class DefaultExecutionTransitionPolicy implements ExecutionTransitionPolicy {

    @Override
    public TransitionDecision evaluate(ExecutionLifecycleState from, ControlAction action, Map<String, Object> context) {
        if (from == null || action == null) {
            return TransitionDecision.deny("Source state or action cannot be null");
        }

        if (from.isTerminal()) {
            return TransitionDecision.deny("Cannot transition from terminal state: " + from);
        }

        switch (action) {
            case START -> {
                if (from == ExecutionLifecycleState.CREATED || from == ExecutionLifecycleState.ADMITTED || from == ExecutionLifecycleState.SCHEDULED) {
                    return TransitionDecision.allow(ExecutionLifecycleState.RUNNING);
                }
                return TransitionDecision.deny("Cannot start execution from state: " + from);
            }
            case PAUSE -> {
                if (from == ExecutionLifecycleState.RUNNING) {
                    return TransitionDecision.allow(ExecutionLifecycleState.PAUSED);
                }
                return TransitionDecision.deny("Can only pause a RUNNING execution, current: " + from);
            }
            case RESUME -> {
                if (from == ExecutionLifecycleState.PAUSED || from == ExecutionLifecycleState.MIGRATING || from == ExecutionLifecycleState.CHECKPOINTED) {
                    return TransitionDecision.allow(ExecutionLifecycleState.RUNNING);
                }
                return TransitionDecision.deny("Cannot resume from state: " + from);
            }
            case CHECKPOINT -> {
                if (from == ExecutionLifecycleState.RUNNING || from == ExecutionLifecycleState.PAUSED) {
                    return TransitionDecision.allow(ExecutionLifecycleState.CHECKPOINTED);
                }
                return TransitionDecision.deny("Cannot checkpoint execution in state: " + from);
            }
            case MIGRATE -> {
                if (from == ExecutionLifecycleState.RUNNING || from == ExecutionLifecycleState.PAUSED || from == ExecutionLifecycleState.CHECKPOINTED) {
                    return TransitionDecision.allow(ExecutionLifecycleState.MIGRATING);
                }
                return TransitionDecision.deny("Cannot migrate execution in state: " + from);
            }
            case RETRY, RECOVER -> {
                if (from == ExecutionLifecycleState.FAILED || from == ExecutionLifecycleState.RUNNING) {
                    return TransitionDecision.allow(ExecutionLifecycleState.RECOVERING);
                }
                return TransitionDecision.deny("Cannot recover execution in state: " + from);
            }
            case CANCEL -> {
                return TransitionDecision.allow(ExecutionLifecycleState.CANCELLED);
            }
            case TERMINATE -> {
                return TransitionDecision.allow(ExecutionLifecycleState.TERMINATED);
            }
            case QUARANTINE -> {
                return TransitionDecision.allow(ExecutionLifecycleState.QUARANTINED);
            }
            case CONTINUE -> {
                return TransitionDecision.allow(from);
            }
            default -> {
                return TransitionDecision.deny("Unsupported action: " + action);
            }
        }
    }
}
