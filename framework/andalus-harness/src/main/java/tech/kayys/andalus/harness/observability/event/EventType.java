package tech.kayys.andalus.harness.observability.event;

import java.util.Objects;

/**
 * Categorization of event types across domains using structured names (e.g. andalus.tool.completed).
 */
public record EventType(EventDomain domain, String action) {
    public EventType {
        Objects.requireNonNull(domain, "domain");
        Objects.requireNonNull(action, "action");
    }

    public static EventType of(EventDomain domain, String action) {
        return new EventType(domain, action);
    }

    public String canonicalName() {
        return "andalus." + domain.name().toLowerCase() + "." + action.toLowerCase();
    }
}
