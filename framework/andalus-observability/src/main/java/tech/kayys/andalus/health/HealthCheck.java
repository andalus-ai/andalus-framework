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


/**
 * Health Check System
 */
public interface HealthCheck {
    
    String name();
    
    HealthResult check() throws Exception;
    
    default boolean isEnabled() { return true; }
}