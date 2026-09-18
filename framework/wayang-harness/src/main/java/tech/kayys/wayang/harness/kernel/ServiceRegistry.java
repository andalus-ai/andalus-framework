package tech.kayys.wayang.harness.kernel;

import java.util.Optional;

/**
 * Service registry interface for registering and resolving runtime services.
 */
public interface ServiceRegistry {

    <T> void register(ServiceKey<T> key, T service);

    default <T> void register(Class<T> type, T service) {
        register(ServiceKey.of(type), service);
    }

    <T> Optional<T> resolve(ServiceKey<T> key);

    default <T> Optional<T> resolve(Class<T> type) {
        return resolve(ServiceKey.of(type));
    }

    <T> T require(ServiceKey<T> key);

    default <T> T require(Class<T> type) {
        return require(ServiceKey.of(type));
    }

    boolean contains(ServiceKey<?> key);

    default boolean contains(Class<?> type) {
        return contains(ServiceKey.of(type));
    }
}
