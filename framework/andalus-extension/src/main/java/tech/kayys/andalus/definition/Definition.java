package tech.kayys.andalus.definition;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.*;

import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.extension.Reference;
import tech.kayys.andalus.descriptor.Descriptor;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.resource.BaseResource;
import tech.kayys.andalus.resource.ResourceType;

/**
 * Base definition for all definable resources.
 * Definitions are immutable and specify what a resource is.
 */
public abstract class Definition extends BaseResource {
    
    private final Set<Reference> dependencies;
    private final Map<String, Object> configuration;
    
    protected Definition(ResourceId id, Metadata metadata, Set<Reference> dependencies, Map<String, Object> configuration) {
        super(id, metadata);
        this.dependencies = dependencies != null 
            ? Collections.unmodifiableSet(new HashSet<>(dependencies)) 
            : Collections.emptySet();
        this.configuration = configuration != null 
            ? Collections.unmodifiableMap(new HashMap<>(configuration)) 
            : Collections.emptyMap();
    }
    
    public Set<Reference> dependencies() {
        return dependencies;
    }
    
    public Map<String, Object> configuration() {
        return configuration;
    }
    
    public abstract Descriptor descriptor();
    
    public boolean hasDependency(ResourceId id) {
        return dependencies.stream().anyMatch(dep -> dep.matches(id.value()));
    }
    
    public boolean hasDependency(ResourceType type) {
        return dependencies.stream().anyMatch(dep -> dep.matches(type));
    }
}