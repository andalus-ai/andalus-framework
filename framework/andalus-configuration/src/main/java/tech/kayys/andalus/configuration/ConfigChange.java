package tech.kayys.andalus.configuration;
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
import java.util.Map;

/**
 * Configuration Change - audit trail
 */
public record ConfigChange(
    String id,
    String type,
    String description,
    Map<String, Object> oldValues,
    Map<String, Object> newValues,
    Instant timestamp
) {}