package tech.kayys.andalus.input;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.List;
import java.util.Map;

import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.resource.Artifact;

/**
 * Input Result
 */
public record InputResult(
    String id,
    InputType type,
    String content,
    Map<String, Object> metadata,
    List<Artifact> artifacts,
    boolean isComplete
) {
    public static InputResult of(String content) {
        return new InputResult(
            Id.random().asString(),
            InputType.TEXT,
            content,
            Map.of(),
            List.of(),
            true
        );
    }
    
    public static InputResult of(String content, InputType type) {
        return new InputResult(
            Id.random().asString(),
            type,
            content,
            Map.of(),
            List.of(),
            true
        );
    }
}