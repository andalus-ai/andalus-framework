package tech.kayys.andalus.knowledge;

public class DefaultKnowledgeContextFactory implements KnowledgeContextFactory {
    @Override
    public KnowledgeContext create(KnowledgeRuntimeContext runtime) {
        if (runtime == null) {
            return KnowledgeContext.empty();
        }
        return new KnowledgeContext(
                runtime.tenantId(),
                runtime.userId(),
                runtime.agentId(),
                runtime.sessionId(),
                runtime.workspaceId(), // domain mapped to workspaceId
                "default", // scope mapped to "default"
                runtime.asOf(),
                runtime.sourceIds(),
                runtime.attributes()
        );
    }
}
