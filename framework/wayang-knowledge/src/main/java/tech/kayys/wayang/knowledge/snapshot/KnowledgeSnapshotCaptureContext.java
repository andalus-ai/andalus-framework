package tech.kayys.wayang.knowledge.snapshot;

import java.util.List;

/**
 * Represents a knowledge snapshot capture context.
 *
 * <p>Its components capture `knowledge`, `policies`, `rules`, `governance`, `runtime`.</p>
 *
 * @param knowledge the knowledge
 * @param policies the policies
 * @param rules the rules
 * @param governance the governance
 * @param runtime the runtime
 */


public record KnowledgeSnapshotCaptureContext(
        List<KnowledgeSnapshotEntry> knowledge,
        KnowledgePolicySnapshot policies,
        KnowledgeRuleSnapshot rules,
        KnowledgeGovernanceSnapshot governance,
        KnowledgeRuntimeSnapshot runtime
) {

    public KnowledgeSnapshotCaptureContext {
        knowledge = knowledge == null ? List.of() : List.copyOf(knowledge);
    }
}
