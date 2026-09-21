package tech.kayys.andalus.network.endpoint;

import tech.kayys.andalus.communication.api.AgentRef;

import java.util.concurrent.CompletionStage;

public interface AgentEndpointResolver {

    CompletionStage<ResolvedEndpoint> resolve(AgentRef agent);
}
