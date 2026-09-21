package tech.kayys.andalus.migration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import tech.kayys.andalus.extension.Version;
import tech.kayys.andalus.resource.Resource;

/**
 * Migration System for resources
 */
public interface Migration<T extends Resource> {
    
    Version fromVersion();
    
    Version toVersion();
    
    T migrate(T resource) throws Exception;
    
    default boolean supports(T resource) {
        return true;
    }
}