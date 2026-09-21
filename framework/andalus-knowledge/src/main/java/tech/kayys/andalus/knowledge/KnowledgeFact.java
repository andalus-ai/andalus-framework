package tech.kayys.andalus.knowledge;
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
 * Knowledge Fact
 */
public record KnowledgeFact(
    String id,
    String subject,
    String predicate,
    String object,
    double confidence,
    String source,
    Map<String, Object> metadata
) {
    public static KnowledgeFact of(String subject, String predicate, String object) {
        return new KnowledgeFact(
            Id.random().asString(),
            subject,
            predicate,
            object,
            1.0,
            null,
            Map.of()
        );
    }
}