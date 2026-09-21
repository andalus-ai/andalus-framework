package tech.kayys.andalus.core;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.List;
import java.util.Map;
import java.util.Set;

import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.core.Permission;
import tech.kayys.andalus.descriptor.CapabilityDescriptor;
import tech.kayys.andalus.descriptor.Descriptor;
import tech.kayys.andalus.descriptor.ParameterDescriptor;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.resource.ResourceType;

/**
 * Agent Descriptor
 */
public record AgentDescriptor(
    String descriptorId,
    String name,
    String version,
    String description,
    Set<String> tags,
    Set<String> categories,
    Map<String, ParameterDescriptor> inputs,
    Map<String, ParameterDescriptor> outputs,
    List<CapabilityDescriptor> capabilities,
    List<Permission> permissions
) implements Descriptor {
    
    @Override
    public ResourceId id() {
        return new ResourceId.AgentId(Id.fromString(descriptorId));
    }
    
    @Override
    public Metadata metadata() {
        return Metadata.builder()
            .name(name)
            .description(description)
            .version(version)
            .label("type", "agent")
            .build();
    }
    
    @Override
    public ResourceType type() {
        return new ResourceType.Agent();
    }
}
