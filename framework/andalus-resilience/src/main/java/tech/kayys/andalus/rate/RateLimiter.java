package tech.kayys.andalus.rate;
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
 * Rate Limiter
 */
public interface RateLimiter {
    
    boolean tryAcquire();
    
    boolean tryAcquire(int permits);
    
    boolean tryAcquire(long timeoutMs);
    
    boolean tryAcquire(int permits, long timeoutMs);
    
    RateLimiterStats getStats();
    
    void reset();
}
