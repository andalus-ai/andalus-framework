package tech.kayys.andalus.registry;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Small map normalization helpers shared across registry models.
 */
final class SdkMaps {

    private SdkMaps() {
    }

    public static Map<String, Object> orderedCopy(Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            return Map.of();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public static <T> Map<String, T> orderedTypedCopy(Map<String, T> values) {
        if (values == null || values.isEmpty()) {
            return Map.of();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }
}
