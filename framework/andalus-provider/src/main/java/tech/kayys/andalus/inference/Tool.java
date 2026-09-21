package tech.kayys.andalus.inference;
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

import tech.kayys.andalus.extension.Id;

/**
 * Tool for function calling
 */
public record Tool(
    String id,
    String name,
    String description,
    Map<String, Object> parameters,
    Map<String, Object> metadata
) {
    public static Tool of(String name, String description, Map<String, Object> parameters) {
        return new Tool(
            Id.random().asString(),
            name,
            description,
            parameters,
            Map.of()
        );
    }
}