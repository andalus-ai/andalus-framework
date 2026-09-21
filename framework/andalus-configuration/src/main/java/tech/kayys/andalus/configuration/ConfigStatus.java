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
 * Configuration Status
 */
public enum ConfigStatus {
    LOADED,
    MODIFIED,
    RELOADED,
    STALE,
    EMPTY,
    ERROR,
    PENDING,
    DELETED
}