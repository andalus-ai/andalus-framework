package tech.kayys.wayang.execution.governance.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Thread-safe in-memory sink for testing and standalone operation.
 */
public final class InMemoryAuditSink implements AuditSink {

    private final List<SecurityEvent> events =
            Collections.synchronizedList(new ArrayList<>());

    @Override
    public void append(SecurityEvent event) {
        events.add(
                Objects.requireNonNull(
                        event,
                        "event cannot be null"
                )
        );
    }

    public List<SecurityEvent> events() {
        synchronized (events) {
            return List.copyOf(events);
        }
    }

    public void clear() {
        events.clear();
    }
}
