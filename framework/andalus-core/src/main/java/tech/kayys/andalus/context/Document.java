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


import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Document
 */
public record Document  (
    String id,
    String content,
    String title,
    String source,
    Map<String, Object> metadata,
    List<Double> embedding
) {
    public Document {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(content, "content cannot be null");
        if (metadata == null) {
            metadata = Map.of();
        }
        if (embedding == null) {
            embedding = List.of();
        }
    }
    
    public static Document of(String id, String content) {
        return new Document(id, content, null, null, Map.of(), List.of());
    }
    
    public static Document of(String id, String content, String title) {
        return new Document(id, content, title, null, Map.of(), List.of());
    }
}
