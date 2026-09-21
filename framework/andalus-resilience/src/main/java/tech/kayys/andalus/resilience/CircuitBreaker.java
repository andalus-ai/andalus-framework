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


import java.util.concurrent.Callable;

/**
 * Circuit Breaker - prevents cascading failures.
 */
public interface CircuitBreaker {
    
    String name();
    
    CircuitBreakerState state();
    
    boolean isOpen();
    
    boolean isClosed();
    
    boolean isHalfOpen();
    
    <T> T execute(Callable<T> callable) throws Exception;
    
    void recordSuccess();
    
    void recordFailure();
    
    void reset();
    
    CircuitBreakerConfig config();
}

