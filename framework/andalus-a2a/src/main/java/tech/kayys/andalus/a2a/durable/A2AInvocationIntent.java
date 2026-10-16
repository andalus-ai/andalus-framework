package tech.kayys.andalus.a2a.durable;

import java.util.Objects;
import java.util.Optional;

/**
 * Durable identity of one logical A2A invocation (6.16.2.8).
 *
 * <p>Carries the correlation between the local execution, logical operation,
 * stable idempotency key, and remote A2A task identity.</p>
 */
public record A2AInvocationIntent(
        String executionId,
        String operationId,
        String taskId,
        String remoteEndpoint,
        Optional<String> idempotencyKey
) {

    /** A2A metadata propagation keys (6.16.2.16). */
    public static final String META_EXECUTION_ID = "andalus.execution.id";
    public static final String META_OPERATION_ID = "andalus.operation.id";
    public static final String META_A2A_TASK_ID = "andalus.a2a.task.id";
    public static final String META_IDEMPOTENCY_KEY = "andalus.idempotency.key";


    public A2AInvocationIntent {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(operationId, "operationId");
        Objects.requireNonNull(taskId, "taskId");
        Objects.requireNonNull(remoteEndpoint, "remoteEndpoint");
        idempotencyKey = idempotencyKey == null ? Optional.empty() : idempotencyKey;

        if (taskId.isBlank()) {
            throw new IllegalArgumentException("taskId must not be blank");
        }

        if (remoteEndpoint.isBlank()) {
            throw new IllegalArgumentException("remoteEndpoint must not be blank");
        }
    }

    public static A2AInvocationIntent keyed(
            String executionId,
            String operationId,
            String taskId,
            String remoteEndpoint,
            String idempotencyKey
    ) {
        return new A2AInvocationIntent(
                executionId,
                operationId,
                taskId,
                remoteEndpoint,
                Optional.of(Objects.requireNonNull(idempotencyKey, "idempotencyKey"))
        );
    }

    public static A2AInvocationIntent withoutKey(
            String executionId,
            String operationId,
            String taskId,
            String remoteEndpoint
    ) {
        return new A2AInvocationIntent(
                executionId,
                operationId,
                taskId,
                remoteEndpoint,
                Optional.empty()
        );
    }

    /**
     * Builds the A2A protocol metadata map carrying the durable invocation identity
     * (6.16.2.16), suitable for inclusion in the outbound {@code A2AMessage}.
     */
    public java.util.Map<String, Object> toMetadata() {
        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put(META_EXECUTION_ID, executionId);
        metadata.put(META_OPERATION_ID, operationId);
        metadata.put(META_A2A_TASK_ID, taskId);
        idempotencyKey.ifPresent(key -> metadata.put(META_IDEMPOTENCY_KEY, key));
        return java.util.Map.copyOf(metadata);
    }
}
