package tech.kayys.andalus.a2ui.andalus;

import tech.kayys.andalus.a2ui.andalus.session.SessionConfigDecoder;

import java.util.Map;

/**
 * Public compatibility facade for stored or remote A2UI session configuration decoding.
 */
public final class WayangA2uiSessionConfigDecoder {

    public static WayangA2uiSessionConfig fromMap(Map<?, ?> values) {
        return SessionConfigDecoder.fromMap(values);
    }

    public static WayangA2uiSessionConfig fromJson(String json) {
        return SessionConfigDecoder.fromJson(json);
    }

    private WayangA2uiSessionConfigDecoder() {
    }
}
