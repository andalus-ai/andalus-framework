package tech.kayys.andalus.knowledge;

import java.util.concurrent.CompletionStage;

/**
 * Service managing human and automated knowledge mutations and lifecycles.
 */
public interface KnowledgeMutationService {

    CompletionStage<KnowledgeMutationResult> apply(KnowledgeMutationRequest request);
}
