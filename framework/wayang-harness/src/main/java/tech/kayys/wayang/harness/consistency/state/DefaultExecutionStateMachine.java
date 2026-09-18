package tech.kayys.wayang.harness.consistency.state;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Standard implementation of {@link ExecutionStateMachine} enforcing the formal Wayang transition graph.
 */
public class DefaultExecutionStateMachine implements ExecutionStateMachine {

    private static final Map<ExecutionState, Set<ExecutionState>> ALLOWED_TRANSITIONS = new EnumMap<>(ExecutionState.class);

    static {
        ALLOWED_TRANSITIONS.put(ExecutionState.CREATED, EnumSet.of(ExecutionState.ADMITTED, ExecutionState.FAILED));
        ALLOWED_TRANSITIONS.put(ExecutionState.ADMITTED, EnumSet.of(ExecutionState.INITIALIZING, ExecutionState.FAILED, ExecutionState.CANCEL_REQUESTED));
        ALLOWED_TRANSITIONS.put(ExecutionState.INITIALIZING, EnumSet.of(ExecutionState.READY, ExecutionState.FAILED, ExecutionState.CANCEL_REQUESTED));
        ALLOWED_TRANSITIONS.put(ExecutionState.READY, EnumSet.of(ExecutionState.RUNNING, ExecutionState.FAILED, ExecutionState.CANCEL_REQUESTED));
        ALLOWED_TRANSITIONS.put(ExecutionState.RUNNING, EnumSet.of(ExecutionState.CHECKPOINTING, ExecutionState.COMPLETING, ExecutionState.PAUSED, ExecutionState.FAILED, ExecutionState.CANCEL_REQUESTED));
        ALLOWED_TRANSITIONS.put(ExecutionState.CHECKPOINTING, EnumSet.of(ExecutionState.RUNNING, ExecutionState.FAILED));
        ALLOWED_TRANSITIONS.put(ExecutionState.PAUSED, EnumSet.of(ExecutionState.RUNNING, ExecutionState.CANCEL_REQUESTED));
        ALLOWED_TRANSITIONS.put(ExecutionState.COMPLETING, EnumSet.of(ExecutionState.COMMITTED, ExecutionState.FAILED));
        ALLOWED_TRANSITIONS.put(ExecutionState.FAILED, EnumSet.of(ExecutionState.RETRYING, ExecutionState.TERMINAL_FAILURE));
        ALLOWED_TRANSITIONS.put(ExecutionState.RETRYING, EnumSet.of(ExecutionState.RUNNING, ExecutionState.FAILED, ExecutionState.TERMINAL_FAILURE));
        ALLOWED_TRANSITIONS.put(ExecutionState.CANCEL_REQUESTED, EnumSet.of(ExecutionState.CANCELING, ExecutionState.CANCELLED));
        ALLOWED_TRANSITIONS.put(ExecutionState.CANCELING, EnumSet.of(ExecutionState.CANCELLED, ExecutionState.FAILED));
        ALLOWED_TRANSITIONS.put(ExecutionState.COMMITTED, EnumSet.noneOf(ExecutionState.class));
        ALLOWED_TRANSITIONS.put(ExecutionState.CANCELLED, EnumSet.noneOf(ExecutionState.class));
        ALLOWED_TRANSITIONS.put(ExecutionState.TERMINAL_FAILURE, EnumSet.noneOf(ExecutionState.class));
    }

    @Override
    public boolean isAllowed(ExecutionState from, ExecutionState to) {
        if (from == null || to == null) {
            return false;
        }
        if (from == to) {
            return true;
        }
        Set<ExecutionState> allowed = ALLOWED_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    @Override
    public TransitionResult transition(ExecutionRecord current, ExecutionState target, TransitionContext context) {
        Objects.requireNonNull(current, "Current execution record cannot be null");
        Objects.requireNonNull(target, "Target execution state cannot be null");

        if (!isAllowed(current.state(), target)) {
            return TransitionResult.rejected("Illegal state transition from " + current.state() + " to " + target);
        }

        if (current instanceof DefaultExecutionRecord record) {
            DefaultExecutionRecord updated = record.withState(target);
            return TransitionResult.success(updated);
        }

        return TransitionResult.rejected("Unsupported record implementation: " + current.getClass().getName());
    }
}
