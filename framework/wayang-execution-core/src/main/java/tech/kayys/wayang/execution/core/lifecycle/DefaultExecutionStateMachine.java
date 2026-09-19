package tech.kayys.wayang.execution.core.lifecycle;

import tech.kayys.wayang.execution.lifecycle.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe execution state machine with transition rule validation.
 */
public class DefaultExecutionStateMachine implements ExecutionStateMachine {

    private final Map<String, ExecutionState> stateMap = new ConcurrentHashMap<>();
    private final Map<String, List<ExecutionTransition>> historyMap = new ConcurrentHashMap<>();

    private static final Map<ExecutionState, Set<ExecutionState>> ALLOWED_TRANSITIONS = new EnumMap<>(ExecutionState.class);

    static {
        ALLOWED_TRANSITIONS.put(ExecutionState.CREATED, Set.of(ExecutionState.ADMITTED, ExecutionState.CANCELLED, ExecutionState.FAILED));
        ALLOWED_TRANSITIONS.put(ExecutionState.ADMITTED, Set.of(ExecutionState.STARTING, ExecutionState.CANCELLED, ExecutionState.FAILED));
        ALLOWED_TRANSITIONS.put(ExecutionState.STARTING, Set.of(ExecutionState.RUNNING, ExecutionState.FAILED, ExecutionState.CRASHED));
        ALLOWED_TRANSITIONS.put(ExecutionState.RUNNING, Set.of(
                ExecutionState.CHECKPOINTING,
                ExecutionState.PAUSED,
                ExecutionState.COMPLETED,
                ExecutionState.FAILED,
                ExecutionState.CRASHED,
                ExecutionState.LOST,
                ExecutionState.PREEMPTED,
                ExecutionState.CANCELLED
        ));
        ALLOWED_TRANSITIONS.put(ExecutionState.CHECKPOINTING, Set.of(
                ExecutionState.RUNNING,
                ExecutionState.PAUSED,
                ExecutionState.FAILED,
                ExecutionState.CRASHED
        ));
        ALLOWED_TRANSITIONS.put(ExecutionState.PAUSED, Set.of(ExecutionState.RESUMING, ExecutionState.CANCELLED));
        ALLOWED_TRANSITIONS.put(ExecutionState.RESUMING, Set.of(ExecutionState.RUNNING, ExecutionState.FAILED, ExecutionState.CRASHED));
        ALLOWED_TRANSITIONS.put(ExecutionState.CRASHED, Set.of(ExecutionState.RECOVERABLE, ExecutionState.FAILED));
        ALLOWED_TRANSITIONS.put(ExecutionState.LOST, Set.of(ExecutionState.RECOVERABLE, ExecutionState.FAILED));
        ALLOWED_TRANSITIONS.put(ExecutionState.PREEMPTED, Set.of(ExecutionState.RECOVERABLE, ExecutionState.CANCELLED));
        ALLOWED_TRANSITIONS.put(ExecutionState.RECOVERABLE, Set.of(ExecutionState.RESUMING, ExecutionState.STARTING, ExecutionState.FAILED));
        ALLOWED_TRANSITIONS.put(ExecutionState.COMPLETED, Set.of());
        ALLOWED_TRANSITIONS.put(ExecutionState.FAILED, Set.of());
        ALLOWED_TRANSITIONS.put(ExecutionState.CANCELLED, Set.of());
    }

    @Override
    public ExecutionState state(String executionId) {
        return stateMap.getOrDefault(executionId, ExecutionState.CREATED);
    }

    @Override
    public TransitionResult transition(String executionId, ExecutionTransition transition) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(transition, "transition cannot be null");

        return stateMap.compute(executionId, (id, current) -> {
            ExecutionState fromState = current == null ? ExecutionState.CREATED : current;
            if (fromState != transition.from()) {
                return fromState;
            }

            if (!isValidTransition(fromState, transition.to())) {
                return fromState;
            }

            historyMap.computeIfAbsent(executionId, k -> new ArrayList<>()).add(transition);
            return transition.to();
        }).equals(transition.to())
                ? TransitionResult.success(transition.to())
                : TransitionResult.failure(state(executionId), "Invalid transition from " + transition.from() + " to " + transition.to());
    }

    @Override
    public boolean isValidTransition(ExecutionState from, ExecutionState to) {
        Set<ExecutionState> allowed = ALLOWED_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public List<ExecutionTransition> history(String executionId) {
        List<ExecutionTransition> list = historyMap.get(executionId);
        return list == null ? List.of() : List.copyOf(list);
    }
}
