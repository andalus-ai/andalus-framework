package tech.kayys.wayang.execution.governance.limits;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

public final class SemaphoreConcurrencyLimiter implements ConcurrencyLimiter {

    private final ConcurrentMap<String, Semaphore> semaphores =
            new ConcurrentHashMap<>();

    @Override
    public LimitCheckResult tryAcquire(
            LimitDefinition definition,
            LimitContext context) {

        Objects.requireNonNull(
                definition,
                "definition cannot be null"
        );

        Objects.requireNonNull(
                context,
                "context cannot be null"
        );

        if (definition.type() != LimitType.CONCURRENCY) {
            throw new IllegalArgumentException(
                    "Definition is not a CONCURRENCY limit"
            );
        }

        String key = key(definition, context);

        Semaphore semaphore = semaphores.computeIfAbsent(
                key,
                ignored -> new Semaphore(
                        Math.toIntExact(definition.capacity())
                )
        );

        if (!semaphore.tryAcquire()) {
            return new LimitCheckResult(
                    LimitDecision.DENY,
                    definition.id(),
                    "Concurrency limit exceeded",
                    null
            );
        }

        return new LimitCheckResult(
                LimitDecision.ALLOW,
                definition.id(),
                "",
                null
        );
    }

    @Override
    public void release(
            LimitDefinition definition,
            LimitContext context) {

        String key = key(definition, context);
        Semaphore semaphore = semaphores.get(key);

        if (semaphore != null) {
            semaphore.release();
        }
    }

    private static String key(
            LimitDefinition definition,
            LimitContext context) {

        String tenant = context.policyContext().tenantId() != null
                ? context.policyContext().tenantId()
                : "default";
        return definition.id() + ":" + tenant;
    }
}
