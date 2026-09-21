package tech.kayys.andalus.health;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.Map;

/**
 * Health Registry
 */
public interface HealthRegistry {
    void register(HealthCheck check);
    void unregister(String name);
    HealthResult check(String name) throws Exception;
    Map<String, HealthResult> checkAll() throws Exception;
    Map<String, HealthResult> checkAllWithTimeout(long timeoutMs) throws Exception;
    boolean isHealthy();
    boolean isReady();
}
