package tech.kayys.andalus.harness.fabric;

import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.mobility.FencingToken;

import java.time.Instant;
import java.util.Objects;

/**
 * Idempotency record for a worker completion spanning independently durable stores.
 * Physical attempt identity is explicit because harness AttemptId and execution-api
 * AttemptId are different types and must not be silently conflated.
 */
public record CompletionIntent(
        String idempotencyKey,
        String executionId,
        AttemptId physicalAttemptId,
        AssignmentId assignmentId,
        long assignmentFenceGeneration,
        TaskClaimToken queueClaimToken,
        FencingToken executionFence,
        CompletionIntentStage stage,
        Instant createdAt
) {
    public CompletionIntent {
        if (idempotencyKey == null || idempotencyKey.isBlank())
            throw new IllegalArgumentException("idempotencyKey cannot be blank");
        if (executionId == null || executionId.isBlank())
            throw new IllegalArgumentException("executionId cannot be blank");
        Objects.requireNonNull(physicalAttemptId, "physicalAttemptId cannot be null");
        Objects.requireNonNull(assignmentId, "assignmentId cannot be null");
        Objects.requireNonNull(queueClaimToken, "queueClaimToken cannot be null");
        Objects.requireNonNull(executionFence, "executionFence cannot be null");
        Objects.requireNonNull(stage, "stage cannot be null");
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        if (assignmentFenceGeneration < 0)
            throw new IllegalArgumentException("assignmentFenceGeneration must be non-negative");
        if (!assignmentId.equals(queueClaimToken.assignmentId()))
            throw new IllegalArgumentException("queue claim must belong to the intent assignment");
    }

    public CompletionIntent advance(CompletionIntentStage next) {
        Objects.requireNonNull(next, "next cannot be null");
        if (next.ordinal() != stage.ordinal() + 1)
            throw new IllegalArgumentException("Completion intent stage must advance exactly once");
        return new CompletionIntent(idempotencyKey, executionId, physicalAttemptId,
                assignmentId, assignmentFenceGeneration, queueClaimToken, executionFence,
                next, createdAt);
    }

    public boolean hasSameOperation(CompletionIntent other) {
        return other != null
                && idempotencyKey.equals(other.idempotencyKey)
                && executionId.equals(other.executionId)
                && physicalAttemptId.equals(other.physicalAttemptId)
                && assignmentId.equals(other.assignmentId)
                && assignmentFenceGeneration == other.assignmentFenceGeneration
                && queueClaimToken.equals(other.queueClaimToken)
                && executionFence.equals(other.executionFence);
    }
}
