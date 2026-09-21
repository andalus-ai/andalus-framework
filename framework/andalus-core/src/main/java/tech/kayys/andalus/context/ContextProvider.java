package tech.kayys.andalus.context;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.Set;
import java.util.concurrent.CompletableFuture;

import tech.kayys.andalus.agent.AgentContext;
import tech.kayys.andalus.extension.Extension;

/**
 * Context Provider - loads contextual data.
 */
public interface ContextProvider extends Extension {
    
    /**
     * Load context data
     */
    ContextData load(AgentContext context) throws Exception;
    
    /**
     * Load context data with a specific query
     */
    default ContextData loadWithQuery(AgentContext context, String query) throws Exception {
        return load(context);
    }
    
    /**
     * Load context data asynchronously returning a CompletionStage
     */
    default java.util.concurrent.CompletionStage<ContextData> loadStage(AgentContext context) {
        return loadAsync(context);
    }
    
    /**
     * Load context data asynchronously
     */
    default CompletableFuture<ContextData> loadAsync(AgentContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return load(context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * Get supported context types
     */
    default Set<ContextType> getSupportedTypes() {
        return Set.of(ContextType.VECTOR_RAG);
    }
    
    /**
     * Check if context is available
     */
    default boolean isAvailable(AgentContext context) {
        return true;
    }
}