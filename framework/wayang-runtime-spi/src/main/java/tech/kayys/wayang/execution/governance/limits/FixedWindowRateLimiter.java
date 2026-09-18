package tech.kayys.wayang.execution.governance.limits;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public final class FixedWindowRateLimiter implements RateLimiter {

    private final Clock clock;
    private final ConcurrentMap<String, Window> windows =
            new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(Clock clock) {
        this.clock = Objects.requireNonNull(
                clock,
                "clock cannot be null"
        );
    }

    public static FixedWindowRateLimiter systemUtc() {
        return new FixedWindowRateLimiter(Clock.systemUTC());
    }

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

        if (definition.type() != LimitType.RATE) {
            throw new IllegalArgumentException(
                    "Definition is not a RATE limit"
            );
        }

        Duration windowDuration = Objects.requireNonNull(
                definition.window(),
                "RATE window cannot be null"
        );

        Instant now = clock.instant();
        String key = key(definition, context);

        Window window = windows.computeIfAbsent(
                key,
                ignored -> new Window(
                        now,
                        new AtomicLong()
                )
        );

        synchronized (window) {
            if (!now.isBefore(window.startedAt().plus(windowDuration))) {
                window.reset(now);
            }

            long count = window.count().incrementAndGet();

            if (count > definition.capacity()) {
                return new LimitCheckResult(
                        LimitDecision.DENY,
                        definition.id(),
                        "Rate limit exceeded",
                        window.startedAt().plus(windowDuration)
                );
            }

            return new LimitCheckResult(
                    LimitDecision.ALLOW,
                    definition.id(),
                    "",
                    null
            );
        }
    }

    @Override
    public void release(
            LimitDefinition definition,
            LimitContext context) {
        // Rate consumption is not released
    }

    private static String key(
            LimitDefinition definition,
            LimitContext context) {

        String tenant = context.policyContext().tenantId() != null
                ? context.policyContext().tenantId()
                : "default";
        String tool = context.toolName() != null ? context.toolName() : "all";

        return definition.id() + ":" + tenant + ":" + tool;
    }

    private static final class Window {

        private Instant startedAt;
        private final AtomicLong count;

        private Window(
                Instant startedAt,
                AtomicLong count) {

            this.startedAt = startedAt;
            this.count = count;
        }

        Instant startedAt() {
            return startedAt;
        }

        AtomicLong count() {
            return count;
        }

        void reset(Instant instant) {
            startedAt = instant;
            count.set(0);
        }
    }
}
