package tech.kayys.wayang.knowledge.snapshot;

import java.util.Map;

/**
 * Represents a knowledge runtime snapshot.
 *
 * <p>Its components capture `runtime version`, `knowledge engine version`, `ranking version`, `compression version`, `budget policy version`, and other values.</p>
 *
 * @param runtimeVersion the runtime version
 * @param knowledgeEngineVersion the knowledge engine version
 * @param rankingVersion the ranking version
 * @param compressionVersion the compression version
 * @param budgetPolicyVersion the budget policy version
 * @param modelProviderId the model provider id
 * @param modelId the model id
 * @param modelVersion the model version
 * @param configuration the configuration
 */


public record KnowledgeRuntimeSnapshot(
        String runtimeVersion,
        String knowledgeEngineVersion,
        String rankingVersion,
        String compressionVersion,
        String budgetPolicyVersion,
        String modelProviderId,
        String modelId,
        String modelVersion,
        Map<String, Object> configuration
) {

    public KnowledgeRuntimeSnapshot {
        configuration = configuration == null ? Map.of() : Map.copyOf(configuration);
    }
}
