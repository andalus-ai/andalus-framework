package tech.kayys.wayang.knowledge.snapshot.delta;

import java.util.Arrays;
import java.util.Map;

/**
 * Represents a knowledge answer resolution state delta operation.
 *
 * <p>Its components capture `type`, `key`, `previous fingerprint`, `value fingerprint`, `value`, and other values.</p>
 *
 * @param type the type
 * @param key the key
 * @param previousFingerprint the previous fingerprint
 * @param valueFingerprint the value fingerprint
 * @param value the value
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionStateDeltaOperation(
        Type type,
        String key,
        String previousFingerprint,
        String valueFingerprint,
        byte[] value,
        Map<String, String> metadata
) {
    /**
     * Enumerates the type values used by the Wayang framework.
     */

    public enum Type {
        PUT,
        REMOVE,
        REPLACE
    }

    public KnowledgeAnswerResolutionStateDeltaOperation {
        value = value == null ? null : Arrays.copyOf(value, value.length);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    @Override
    public byte[] value() {
        return value == null ? null : Arrays.copyOf(value, value.length);
    }
}
