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


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import tech.kayys.andalus.extension.Id;

/**
 * Prompt - complete prompt model
 */
public record Prompt(
    String id,
    String template,
    String rendered,
    Map<String, Object> variables,
    Map<String, Object> metadata,
    PromptType type,
    Instant timestamp
) {
    public static Prompt of(String template, Map<String, Object> variables) {
        return new Prompt(
            Id.random().asString(),
            template,
            null,
            variables,
            Map.of(),
            PromptType.TEXT,
            Instant.now()
        );
    }
    
    public static Prompt ofRendered(String rendered) {
        return new Prompt(
            Id.random().asString(),
            null,
            rendered,
            Map.of(),
            Map.of(),
            PromptType.TEXT,
            Instant.now()
        );
    }
    
    public Prompt withVariable(String key, Object value) {
        Map<String, Object> newVariables = new HashMap<>(variables);
        newVariables.put(key, value);
        return new Prompt(id, template, rendered, newVariables, metadata, type, timestamp);
    }
    
    public Prompt withRendered(String rendered) {
        return new Prompt(id, template, rendered, variables, metadata, type, timestamp);
    }
}
