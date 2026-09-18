package tech.kayys.wayang.harness.kernel;

import java.util.Objects;

/**
 * Type-safe lookup key for services registered in the kernel service registry.
 *
 * @param <T> service type
 */
public record ServiceKey<T>(Class<T> type, String qualifier) {

    public ServiceKey {
        Objects.requireNonNull(type, "Service type cannot be null");
        qualifier = qualifier != null ? qualifier : "default";
    }

    public static <T> ServiceKey<T> of(Class<T> type) {
        return new ServiceKey<>(type, "default");
    }

    public static <T> ServiceKey<T> of(Class<T> type, String qualifier) {
        return new ServiceKey<>(type, qualifier);
    }
}
