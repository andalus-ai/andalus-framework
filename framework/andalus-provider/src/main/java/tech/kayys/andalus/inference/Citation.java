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
 * Citation
 */
public record Citation(
    String id,
    String source,
    String content,
    String url,
    Map<String, Object> metadata
) {
    public static Citation of(String source, String content) {
        return new Citation(Id.random().asString(), source, content, null, Map.of());
    }
}
