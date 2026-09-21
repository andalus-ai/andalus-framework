package tech.kayys.andalus.database;
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

/**
 * Migration Record
 */
public record Migration(
    String id,
    String name,
    String version,
    String description,
    String sql,
    Instant appliedAt
) {}