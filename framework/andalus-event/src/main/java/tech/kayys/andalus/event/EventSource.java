package tech.kayys.andalus.event;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.Objects;

import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.resource.ResourceType;

/**
 * Event Source - where the event originated
 */
public interface EventSource {
    Id id();
    ResourceType type();
    String name();
    
    static EventSource of(ResourceId id) {
        return new SimpleEventSource(id.value(), id.type(), id.asString());
    }
    
    static EventSource of(Id id, ResourceType type, String name) {
        return new SimpleEventSource(id, type, name);
    }
    
    record SimpleEventSource(
        Id id,
        ResourceType type,
        String name
    ) implements EventSource {
        public SimpleEventSource {
            Objects.requireNonNull(id, "id cannot be null");
            Objects.requireNonNull(type, "type cannot be null");
        }
    }
}
