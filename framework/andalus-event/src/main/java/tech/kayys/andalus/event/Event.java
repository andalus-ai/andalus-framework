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


import java.time.Instant;
import java.util.*;

import tech.kayys.andalus.identity.ResourceId.EventId;
import tech.kayys.andalus.identity.ResourceId.CorrelationId;
import tech.kayys.andalus.identity.ResourceId.CausationId;
import tech.kayys.andalus.resource.Resource;

/**
 * Event - the primary communication mechanism in Andalus.
 * Everything communicates through events.
 */
public interface Event extends Resource {
    
    EventId id();
    
    EventType eventType();
    
    Instant occurredAt();
    
    EventSource source();
    
    CorrelationId correlationId();
    
    CausationId causationId();
    
    EventPayload payload();
    
    EventMetadata eventMetadata();
    
    default String correlation() {
        return correlationId().asString();
    }
    
    default String causation() {
        return causationId().asString();
    }
}