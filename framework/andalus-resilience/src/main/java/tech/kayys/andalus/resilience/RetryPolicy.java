package tech.kayys.andalus.resilience;
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
 * Retry System
 */
public interface RetryPolicy {
    
    boolean shouldRetry(int attempt, Exception error);
    
    long waitDurationMs(int attempt);
    
    int maxAttempts();
}