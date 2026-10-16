package tech.kayys.andalus.knowledge;
import java.util.List;
import java.util.Map;

public record KnowledgeResolutionRequest(
        KnowledgeQuery query,
        KnowledgeContext context,
        List<KnowledgeDefinition> definitions,
        Map<String, Object> attributes
) {
    public KnowledgeResolutionRequest {
        definitions = definitions == null ? List.of() : List.copyOf(definitions);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
    public static KnowledgeResolutionRequest of(KnowledgeQuery query, KnowledgeContext context) {
        return new KnowledgeResolutionRequest(query, context == null ? KnowledgeContext.empty() : context, List.of(), Map.of());
    }
}
