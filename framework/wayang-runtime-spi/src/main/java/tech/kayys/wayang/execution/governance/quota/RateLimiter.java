package tech.kayys.wayang.execution.governance.quota;

/**
 * Strategy interface for rate limiting tool executions per tenant, agent, or user.
 */
public interface RateLimiter {

    /**
     * Attempts to acquire execution permit for the given subject.
     *
     * @param key The rate limit partition key (e.g., tenantId, agentId, or user:tool).
     * @return true if permitted; false if rate limit exceeded.
     */
    boolean tryAcquire(String key);

    /**
     * Resets rate counters for testing or administration.
     */
    void reset();

    /**
     * A permissive rate limiter allowing all executions.
     */
    static RateLimiter unlimited() {
        return new RateLimiter() {
            @Override
            public boolean tryAcquire(String key) { return true; }
            @Override
            public void reset() {}
        };
    }
}
