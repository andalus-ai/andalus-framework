package tech.kayys.andalus.knowledge;
import java.util.concurrent.CompletionStage;

public interface KnowledgeResolutionPolicy {
    CompletionStage<KnowledgeResolutionDecision> evaluate(KnowledgeResolutionRequest request, KnowledgeSource source);
}
