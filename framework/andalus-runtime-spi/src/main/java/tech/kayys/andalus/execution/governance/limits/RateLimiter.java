package tech.kayys.andalus.execution.governance.limits;

public interface RateLimiter {

    LimitCheckResult tryAcquire(
            LimitDefinition definition,
            LimitContext context);

    void release(
            LimitDefinition definition,
            LimitContext context);
}
