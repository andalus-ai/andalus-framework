package tech.kayys.andalus.network.trust;

import java.util.concurrent.CompletionStage;

public interface AgentTrustService {

    CompletionStage<TrustDecision> evaluate(TrustRequest request);
}
