package tech.kayys.andalus.messaging;
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

/**
 * Topic Info
 */
public record TopicInfo(
    String name,
    int partitions,
    int replicationFactor,
    long messagesCount,
    long sizeBytes,
    Map<String, Object> metadata
) {}