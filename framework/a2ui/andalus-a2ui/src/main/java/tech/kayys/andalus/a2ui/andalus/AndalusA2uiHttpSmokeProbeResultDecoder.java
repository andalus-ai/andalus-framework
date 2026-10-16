package tech.kayys.andalus.a2ui.andalus;

import tech.kayys.andalus.a2ui.andalus.transport.TransportJson;
import tech.kayys.andalus.a2ui.andalus.transport.TransportMaps;

import tech.kayys.andalus.a2ui.andalus.support.DecodeValues;

import java.util.Map;

/**
 * Canonical decoder for stored or remote A2UI HTTP smoke probe results.
 */
public final class WayangA2uiHttpSmokeProbeResultDecoder {

    public static WayangA2uiHttpSmokeProbeResult fromMap(Map<?, ?> values) {
        Map<String, Object> copy = TransportMaps.copy(values);
        return new WayangA2uiHttpSmokeProbeResult(
                DecodeValues.nonNegativeInt(copy.get("statusCode"), 0),
                DecodeValues.bool(copy.get("httpSuccessful"), false),
                DecodeValues.text(copy.get("routeOperation")),
                DecodeValues.text(copy.get("allow")),
                DecodeValues.text(copy.get(WayangA2uiTransportFields.OUTCOME)),
                WayangA2uiHttpSmokeSummary.fromMap(TransportMaps.copyMap(copy.get("summary"))),
                TransportMaps.copyMap(copy.get("headers")));
    }

    public static WayangA2uiHttpSmokeProbeResult fromJson(String json) {
        return fromMap(TransportJson.map(
                json,
                "A2UI HTTP smoke probe result JSON must not be blank",
                "Unable to decode A2UI HTTP smoke probe result JSON"));
    }

    private WayangA2uiHttpSmokeProbeResultDecoder() {
    }
}
