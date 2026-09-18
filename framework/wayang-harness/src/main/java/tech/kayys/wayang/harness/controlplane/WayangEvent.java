package tech.kayys.wayang.harness.controlplane;

import tech.kayys.wayang.harness.journal.EventId;

import java.time.Instant;
import java.util.Map;

/**
 * Committed fact emitted within the Wayang control-plane.
 */
public interface WayangEvent {

    EventId id();

    ControlPlaneEventType type();

    EventVersion version();

    Instant timestamp();

    CorrelationId correlationId();

    CausationId causationId();

    Map<String, Object> payload();
}
