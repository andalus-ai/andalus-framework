package tech.kayys.wayang.execution.governance.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory thread-safe implementation of {@link ToolAuditPublisher} useful for testing and verification.
 */
public final class InMemoryToolAuditPublisher implements ToolAuditPublisher {

    private final List<ToolAuditEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void publish(ToolAuditEvent event) {
        Objects.requireNonNull(event, "event cannot be null");
        events.add(event);
    }

    public List<ToolAuditEvent> events() {
        return List.copyOf(events);
    }

    public List<ToolAuditEvent> eventsForTenant(String tenantId) {
        if (tenantId == null) {
            return List.of();
        }
        return events.stream()
                .filter(e -> tenantId.equals(e.tenantId()))
                .toList();
    }

    public List<ToolAuditEvent> eventsForExecution(String executionId) {
        if (executionId == null) {
            return List.of();
        }
        return events.stream()
                .filter(e -> executionId.equals(e.executionId()))
                .toList();
    }

    public void clear() {
        events.clear();
    }

    public int count() {
        return events.size();
    }
}
