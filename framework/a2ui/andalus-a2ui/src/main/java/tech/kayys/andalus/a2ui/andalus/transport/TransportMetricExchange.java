package tech.kayys.andalus.a2ui.andalus.transport;

import tech.kayys.andalus.a2ui.andalus.WayangA2uiTransportOutcome;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiTransportResponse;

import java.util.Map;

/**
 * Minimal transport-facing view shared by A2UI scenario exchange records.
 */
public interface TransportMetricExchange {

    WayangA2uiTransportResponse transportResponse();

    default WayangA2uiTransportOutcome outcome() {
        return transportResponse().outcome();
    }

    default boolean transportError() {
        return transportResponse().transportError().isPresent();
    }

    default Map<String, Object> responseEnvelope() {
        return transportResponse().toMap();
    }
}
