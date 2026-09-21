package tech.kayys.andalus.harness.controlplane;

import tech.kayys.andalus.harness.journal.EventId;

import java.time.Instant;
import java.util.Map;

/**
 * Committed fact emitted within the Andalus control-plane.
 */
public interface AndalusEvent {

    EventId id();

    ControlPlaneEventType type();

    EventVersion version();

    Instant timestamp();

    CorrelationId correlationId();

    CausationId causationId();

    Map<String, Object> payload();
}
