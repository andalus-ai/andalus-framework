package tech.kayys.andalus.execution;

import tech.kayys.andalus.agent.AgentResponse;

import java.util.Objects;

public record AgentLoopResult(
        ExecutionStatus status,
        AgentResponse response
) {

    public AgentLoopResult {
        Objects.requireNonNull(status, "status");
    }

    public boolean completed() {
        return status == ExecutionStatus.COMPLETED;
    }

    public boolean terminal() {
        return status == ExecutionStatus.COMPLETED
                || status == ExecutionStatus.FAILED
                || status == ExecutionStatus.CANCELLED
                || status == ExecutionStatus.TIMEOUT;
    }
}
