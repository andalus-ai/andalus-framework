package tech.kayys.andalus.prompt;
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

import tech.kayys.andalus.context.ContextData;
import tech.kayys.andalus.agent.AgentContext;
import tech.kayys.andalus.extension.Extension;


/**
 * Prompt Provider - builds prompts.
 */
public interface PromptProvider extends Extension {
    
    /**
     * Build a prompt
     */
    Prompt build(AgentContext context, Object plan) throws Exception;
    
    /**
     * Build with context data
     */
    default Prompt build(AgentContext context, Object plan, ContextData contextData) throws Exception {
        return build(context, plan);
    }
    
    /**
     * Get supported prompt templates
     */
    default Set<String> getTemplates() {
        return Set.of("default");
    }
    
    /**
     * Register a template
     */
    default void registerTemplate(String name, String template) throws Exception {
        // Optional
    }
}
