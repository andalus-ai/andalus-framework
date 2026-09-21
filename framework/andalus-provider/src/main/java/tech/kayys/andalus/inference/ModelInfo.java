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
import java.util.Set;

/**
 * Model Info
 */
public record ModelInfo(
    String id,
    String name,
    String provider,
    Set<String> capabilities,
    Map<String, Object> metadata
) {}