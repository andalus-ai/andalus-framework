package tech.kayys.wayang.harness.context;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a context source.
 *
 * <p>Its components capture `source id`, `provider type`, `timestamp`.</p>
 *
 * @param sourceId the source id
 * @param providerType the provider type
 * @param timestamp the timestamp
 */


public record ContextSource(
        String sourceId,
        String providerType,
        Instant timestamp
) {
    public ContextSource {
        Objects.requireNonNull(sourceId, "sourceId");
        providerType = providerType == null ? "unknown" : providerType;
        timestamp = timestamp == null ? Instant.now() : timestamp;
    }

    public static ContextSource of(String sourceId, String providerType) {
        return new ContextSource(sourceId, providerType, Instant.now());
    }
}
