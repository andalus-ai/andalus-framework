package tech.kayys.andalus.knowledge;
import java.util.Objects;
import java.util.UUID;

public record KnowledgeInvocation(
        String invocationId,
        String executionId,
        String turnId,
        String traceId
) {
    public KnowledgeInvocation {
        invocationId = invocationId == null ? UUID.randomUUID().toString() : invocationId;
        executionId = executionId == null ? "" : executionId;
        turnId = turnId == null ? "" : turnId;
        traceId = traceId == null ? "" : traceId;
    }
    public static KnowledgeInvocation forExecution(String executionId, String traceId) {
        return new KnowledgeInvocation(UUID.randomUUID().toString(), executionId, "", traceId);
    }
}
