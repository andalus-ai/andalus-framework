package tech.kayys.andalus.knowledge;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record KnowledgeRuntimeContext(
        String tenantId,
        String userId,
        String agentId,
        String sessionId,
        String executionId,
        String workspaceId,
        String projectId,
        Instant asOf,
        List<String> sourceIds,
        Map<String, Object> attributes
) {
    public KnowledgeRuntimeContext {
        sourceIds = sourceIds == null ? List.of() : List.copyOf(sourceIds);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        asOf = asOf == null ? Instant.now() : asOf;
    }
    public static KnowledgeRuntimeContext of(String agentId, String executionId) {
        return new KnowledgeRuntimeContext(null, null, agentId, null, executionId, null, null, Instant.now(), List.of(), Map.of());
    }
}
