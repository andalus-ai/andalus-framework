package tech.kayys.andalus.network.discovery;

import tech.kayys.andalus.communication.api.AgentDescriptor;

import java.util.concurrent.CompletionStage;

public interface AgentDiscovery {

    CompletionStage<DiscoveryResult> discover(DiscoveryQuery query);

    CompletionStage<AgentDescriptor> resolve(String agentId);
}
