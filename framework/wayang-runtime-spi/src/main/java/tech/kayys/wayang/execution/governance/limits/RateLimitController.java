package tech.kayys.wayang.execution.governance.limits;

import java.util.Objects;

public final class RateLimitController implements LimitController {

    private final LimitDefinition definition;
    private final RateLimiter limiter;

    public RateLimitController(
            LimitDefinition definition,
            RateLimiter limiter) {

        if (definition.type() != LimitType.RATE) {
            throw new IllegalArgumentException(
                    "Definition must be RATE"
            );
        }

        this.definition = Objects.requireNonNull(
                definition,
                "definition cannot be null"
        );

        this.limiter = Objects.requireNonNull(
                limiter,
                "limiter cannot be null"
        );
    }

    @Override
    public LimitReservation acquire(LimitContext context) {
        LimitCheckResult result = limiter.tryAcquire(definition, context);

        if (!result.allowed()) {
            throw new LimitExceededException(
                    result.limitId(),
                    result.reason()
            );
        }

        return new LimitReservation() {
            private boolean completed;

            @Override
            public synchronized void commit() {
                completed = true;
            }

            @Override
            public synchronized void rollback() {
                if (completed) {
                    return;
                }

                limiter.release(definition, context);
                completed = true;
            }

            @Override
            public synchronized boolean active() {
                return !completed;
            }
        };
    }
}
