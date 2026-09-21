package tech.kayys.andalus.service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import tech.kayys.andalus.identity.ResourceId.CorrelationId;
import tech.kayys.andalus.event.Event;
import tech.kayys.andalus.event.EventSource;
import tech.kayys.andalus.event.EventType;

/**
 * Event Filter
 */
@FunctionalInterface
public interface EventFilter {
    boolean matches(Event event);
    
    static EventFilter all() {
        return event -> true;
    }
    
    static EventFilter ofType(EventType type) {
        return event -> event.type().equals(type);
    }
    
    static EventFilter ofSource(EventSource source) {
        return event -> event.source().equals(source);
    }
    
    static EventFilter ofCorrelation(CorrelationId correlationId) {
        return event -> event.correlationId().equals(correlationId);
    }
}
