package tech.kayys.wayang.execution.lifecycle;

public interface ExecutionStateMachine {
    ExecutionState state(String executionId);
    TransitionResult transition(String executionId, ExecutionTransition transition);
    boolean isValidTransition(ExecutionState from, ExecutionState to);
}
