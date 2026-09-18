package tech.kayys.wayang.execution.governance.limits;

public interface ConcurrencyLimiter {

    LimitCheckResult tryAcquire(
            LimitDefinition definition,
            LimitContext context);

    void release(
            LimitDefinition definition,
            LimitContext context);
}
