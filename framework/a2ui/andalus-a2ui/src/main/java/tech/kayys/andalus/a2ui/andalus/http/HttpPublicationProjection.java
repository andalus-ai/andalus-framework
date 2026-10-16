package tech.kayys.andalus.a2ui.andalus.http;

import tech.kayys.andalus.a2ui.andalus.WayangA2uiHttpEndpointPublication;
import tech.kayys.andalus.a2ui.andalus.transport.TransportMaps;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Ordered projection helpers for A2UI HTTP endpoint publication manifests.
 */
public final class HttpPublicationProjection {

    private HttpPublicationProjection() {
    }

    public static Map<String, Object> publication(WayangA2uiHttpEndpointPublication publication) {
        WayangA2uiHttpEndpointPublication resolved = Objects.requireNonNull(publication, "publication");
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("routeCount", resolved.routeCount());
        values.put("operations", resolved.operations());
        values.put("paths", resolved.paths());
        values.put("routes", resolved.routes());
        return TransportMaps.freeze(values);
    }
}
