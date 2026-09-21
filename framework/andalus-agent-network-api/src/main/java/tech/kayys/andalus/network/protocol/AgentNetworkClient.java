package tech.kayys.andalus.network.protocol;

import tech.kayys.andalus.network.AgentNetworkRequest;
import tech.kayys.andalus.network.AgentNetworkResponse;
import tech.kayys.andalus.network.AgentNetworkTask;

import java.util.concurrent.CompletionStage;

public interface AgentNetworkClient {

    CompletionStage<AgentNetworkResponse> invoke(AgentNetworkRequest request);

    CompletionStage<AgentNetworkTask> submit(AgentNetworkRequest request);
}
