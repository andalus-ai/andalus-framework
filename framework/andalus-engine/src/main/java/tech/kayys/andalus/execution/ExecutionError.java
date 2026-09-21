package tech.kayys.andalus.execution;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Execution Error
 */
public record ExecutionError(
    String code,
    String message,
    String phase,
    Throwable cause,
    Map<String, Object> context
) {
    public ExecutionError {
        Objects.requireNonNull(code, "code cannot be null");
        Objects.requireNonNull(message, "message cannot be null");
        if (context == null) {
            context = Collections.emptyMap();
        } else {
            context = Collections.unmodifiableMap(new HashMap<>(context));
        }
    }
    
    public static ExecutionError of(String code, String message) {
        return new ExecutionError(code, message, null, null, Map.of());
    }
    
    public static ExecutionError of(String code, String message, Throwable cause) {
        return new ExecutionError(code, message, null, cause, Map.of());
    }
    
    public static ExecutionError of(String code, String message, String phase) {
        return new ExecutionError(code, message, phase, null, Map.of());
    }
    
    public static ExecutionError of(String code, String message, String phase, Throwable cause) {
        return new ExecutionError(code, message, phase, cause, Map.of());
    }
}
