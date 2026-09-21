package tech.kayys.andalus.execution.governance.limits;

public interface UsageQuota {

    LimitCheckResult tryConsume(
            LimitDefinition definition,
            LimitContext context,
            long amount);
}
