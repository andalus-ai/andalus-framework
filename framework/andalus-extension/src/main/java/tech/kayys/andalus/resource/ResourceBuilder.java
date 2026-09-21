package tech.kayys.andalus.resource;
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

import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.extension.Version;
import tech.kayys.andalus.identity.ResourceId;

import java.time.Instant;

/**
 * Resource builder for creating resources with type safety.
 */
public class ResourceBuilder<T extends Resource> {
    
    private ResourceId id;
    private Metadata metadata;
    private final Map<String, Object> properties = new HashMap<>();
    private final Class<T> resourceType;
    
    private ResourceBuilder(Class<T> resourceType) {
        this.resourceType = resourceType;
    }
    
    public static <T extends Resource> ResourceBuilder<T> of(Class<T> resourceType) {
        return new ResourceBuilder<>(resourceType);
    }
    
    public ResourceBuilder<T> id(ResourceId id) {
        this.id = id;
        return this;
    }
    
    public ResourceBuilder<T> metadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }
    
    public ResourceBuilder<T> name(String name) {
        if (metadata == null) {
            metadata = Metadata.builder().name(name).build();
        } else {
            metadata = new Metadata(
                name,
                metadata.description(),
                metadata.version(),
                metadata.createdAt(),
                metadata.updatedAt(),
                metadata.createdBy(),
                metadata.updatedBy(),
                metadata.labels(),
                metadata.attributes()
            );
        }
        return this;
    }
    
    public ResourceBuilder<T> description(String description) {
        if (metadata == null) {
            metadata = Metadata.builder().description(description).build();
        } else {
            metadata = new Metadata(
                metadata.name(),
                description,
                metadata.version(),
                metadata.createdAt(),
                metadata.updatedAt(),
                metadata.createdBy(),
                metadata.updatedBy(),
                metadata.labels(),
                metadata.attributes()
            );
        }
        return this;
    }
    
    public ResourceBuilder<T> version(Version version) {
        if (metadata == null) {
            metadata = Metadata.builder().version(version).build();
        } else {
            metadata = new Metadata(
                metadata.name(),
                metadata.description(),
                version,
                metadata.createdAt(),
                metadata.updatedAt(),
                metadata.createdBy(),
                metadata.updatedBy(),
                metadata.labels(),
                metadata.attributes()
            );
        }
        return this;
    }
    
    public ResourceBuilder<T> property(String key, Object value) {
        properties.put(key, value);
        return this;
    }
    
    // Build method should be implemented by concrete resource builders
    public T build() {
        if (id == null) {
            id = createDefaultId();
        }
        if (metadata == null) {
            metadata = Metadata.builder().now().build();
        }
        return createResource();
    }
    
    protected ResourceId createDefaultId() {
        // Override in subclasses
        return new ResourceId.CustomId(Id.random(), new ResourceType.Custom(resourceType.getSimpleName()));
    }
    
    protected T createResource() {
        throw new UnsupportedOperationException("Override in subclass");
    }
}
