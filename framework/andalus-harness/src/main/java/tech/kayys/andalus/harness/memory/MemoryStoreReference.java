package tech.kayys.andalus.harness.memory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * References a configured or registered memory store backend without binding to concrete storage implementation.
 */
public record MemoryStoreReference(
        String storeId,
        String storeType,
        Optional<String> version,
        Map<String, Object> configuration
) {

    public MemoryStoreReference {
        Objects.requireNonNull(storeId, "storeId must not be null");
        Objects.requireNonNull(storeType, "storeType must not be null");
        version = version == null ? Optional.empty() : version;
        configuration = configuration == null ? Map.of() : Map.copyOf(configuration);
    }

    public static MemoryStoreReference of(String storeId, String storeType) {
        return new MemoryStoreReference(storeId, storeType, Optional.empty(), Map.of());
    }
}
