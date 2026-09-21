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


import java.util.Set;

/**
 * Knowledge Source Info
 */
public record KnowledgeSourceInfo(
    String name,
    String type,
    Set<String> capabilities
) {}
