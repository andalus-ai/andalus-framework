package tech.kayys.andalus.execution.governance.limits;

public interface ConcurrencyLimiter {

    LimitCheckResult tryAcquire(
            LimitDefinition definition,
            LimitContext context);

    void release(
            LimitDefinition definition,
            LimitContext context);
}
