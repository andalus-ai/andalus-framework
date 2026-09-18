package tech.kayys.wayang.harness.kernel;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe reference implementation of {@link ServiceRegistry}.
 */
public class DefaultServiceRegistry implements ServiceRegistry {

    private final Map<ServiceKey<?>, Object> services = new ConcurrentHashMap<>();

    @Override
    public <T> void register(ServiceKey<T> key, T service) {
        Objects.requireNonNull(key, "ServiceKey cannot be null");
        Objects.requireNonNull(service, "Service instance cannot be null");
        services.put(key, service);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> resolve(ServiceKey<T> key) {
        Objects.requireNonNull(key, "ServiceKey cannot be null");
        Object instance = services.get(key);
        if (instance == null) {
            return Optional.empty();
        }
        return Optional.of((T) instance);
    }

    @Override
    public <T> T require(ServiceKey<T> key) {
        return resolve(key).orElseThrow(() ->
                new NoSuchElementException("Required service not registered: " + key));
    }

    @Override
    public boolean contains(ServiceKey<?> key) {
        Objects.requireNonNull(key, "ServiceKey cannot be null");
        return services.containsKey(key);
    }
}
