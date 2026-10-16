package tech.kayys.andalus.knowledge;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class AllowAllKnowledgeResolutionPolicy implements KnowledgeResolutionPolicy {
    @Override
    public CompletionStage<KnowledgeResolutionDecision> evaluate(KnowledgeResolutionRequest request, KnowledgeSource source) {
        return CompletableFuture.completedFuture(KnowledgeResolutionDecision.allow());
    }
}
