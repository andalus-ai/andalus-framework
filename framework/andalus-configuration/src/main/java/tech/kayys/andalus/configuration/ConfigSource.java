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


/**
 * Configuration Source
 */
public enum ConfigSource {
    FILE,
    ENVIRONMENT,
    SYSTEM_PROPERTIES,
    DATABASE,
    NETWORK,
    RUNTIME,
    MEMORY,
    PLUGIN,
    TENANT,
    DEFAULT,
    MERGED,
    UNKNOWN
}