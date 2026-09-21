package tech.kayys.andalus.extension;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;



import java.time.Instant;
import java.util.Objects;

import tech.kayys.andalus.resource.ResourceType;

/**
 * Represents a reference to another resource in the Andalus ecosystem.
 */
public record Reference(
    Id id,
    ResourceType type,
    String name,
    Version version,
    Instant timestamp
) {
    
    public static Reference of(Id id, ResourceType type) {
        return new Reference(id, type, null, null, null);
    }
    
    public static Reference of(Id id, ResourceType type, String name) {
        return new Reference(id, type, name, null, null);
    }
    
    public static Reference of(Id id, ResourceType type, Version version) {
        return new Reference(id, type, null, version, null);
    }
    
    public static Reference of(Id id, ResourceType type, String name, Version version) {
        return new Reference(id, type, name, version, null);
    }
    
    public boolean matches(Id id) {
        return this.id.equals(id);
    }
    
    public boolean matches(ResourceType type) {
        return this.type == type;
    }
    
    public boolean matches(String name) {
        return this.name != null && this.name.equals(name);
    }
}