package tech.kayys.andalus.a2a.durable;

import java.util.Objects;
import java.util.Optional;

/**
 * Correlates an Andalus Claw execution with an A2A task and, when known, the remote execution (6.16.1.4).
 *
 * <p>Preserves the critical identity separation:
 * <ul>
 *   <li>{@code callerExecutionId} = Canonical local Claw execution</li>
 *   <li>{@code taskId} = A2A protocol task correlation identity</li>
 *   <li>{@code remoteExecutionId} = Remote agent execution identity (when available)</li>
 * </ul>
 */
public record A2AExecutionCorrelation(
        String callerExecutionId,
        String taskId,
        Optional<String> remoteExecutionId
) {

    public A2AExecutionCorrelation {
        Objects.requireNonNull(callerExecutionId, "callerExecutionId");
        Objects.requireNonNull(taskId, "taskId");
        if (taskId.isBlank()) {
            throw new IllegalArgumentException("taskId must not be blank");
        }
        remoteExecutionId = remoteExecutionId == null ? Optional.empty() : remoteExecutionId;
    }

    public static A2AExecutionCorrelation of(String callerExecutionId, String taskId) {
        return new A2AExecutionCorrelation(callerExecutionId, taskId, Optional.empty());
    }

    public A2AExecutionCorrelation withRemoteExecution(String remoteExecutionId) {
        return new A2AExecutionCorrelation(
                callerExecutionId,
                taskId,
                Optional.of(Objects.requireNonNull(remoteExecutionId, "remoteExecutionId"))
        );
    }
}
