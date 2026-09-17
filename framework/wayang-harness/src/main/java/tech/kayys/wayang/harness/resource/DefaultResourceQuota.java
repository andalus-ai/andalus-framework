package tech.kayys.wayang.harness.resource;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe concurrent implementation of {@link ResourceQuota} tracking limits and usage.
 */
public class DefaultResourceQuota implements ResourceQuota {

    private final Map<ResourceType, QuotaLimit> limits = new ConcurrentHashMap<>();
    private final Map<ResourceType, AtomicLong> usages = new ConcurrentHashMap<>();

    public DefaultResourceQuota() {}

    public DefaultResourceQuota(Map<ResourceType, QuotaLimit> initialLimits) {
        if (initialLimits != null) {
            limits.putAll(initialLimits);
        }
    }

    public static DefaultResourceQuota unlimited() {
        return new DefaultResourceQuota();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setLimit(QuotaLimit limit) {
        Objects.requireNonNull(limit, "limit");
        limits.put(limit.type(), limit);
    }

    @Override
    public QuotaLimit limit(ResourceType type) {
        Objects.requireNonNull(type, "type");
        return limits.getOrDefault(type, QuotaLimit.unlimited(type));
    }

    @Override
    public QuotaUsage usage(ResourceType type) {
        Objects.requireNonNull(type, "type");
        AtomicLong counter = usages.get(type);
        long current = counter != null ? counter.get() : 0L;
        String unit = limit(type).unit();
        return QuotaUsage.of(type, current, unit);
    }

    @Override
    public boolean canAllocate(ResourceRequest request) {
        Objects.requireNonNull(request, "request");
        ResourceType type = request.type();
        QuotaLimit lim = limit(type);
        if (lim.isUnlimited()) {
            return true;
        }

        long requestedAmount = request.constraints().maximum().orElse(1L);
        AtomicLong counter = usages.get(type);
        long current = counter != null ? counter.get() : 0L;
        return (current + requestedAmount) <= lim.limit();
    }

    public boolean tryAllocate(ResourceRequest request) {
        Objects.requireNonNull(request, "request");
        ResourceType type = request.type();
        long requestedAmount = request.constraints().maximum().orElse(1L);

        QuotaLimit lim = limit(type);
        if (lim.isUnlimited()) {
            recordUsage(type, requestedAmount);
            return true;
        }

        AtomicLong counter = usages.computeIfAbsent(type, t -> new AtomicLong(0L));
        while (true) {
            long current = counter.get();
            if (current + requestedAmount > lim.limit()) {
                return false;
            }
            if (counter.compareAndSet(current, current + requestedAmount)) {
                return true;
            }
        }
    }

    public void recordUsage(ResourceType type, long amount) {
        Objects.requireNonNull(type, "type");
        usages.computeIfAbsent(type, t -> new AtomicLong(0L)).addAndGet(Math.max(0L, amount));
    }

    public void releaseUsage(ResourceType type, long amount) {
        Objects.requireNonNull(type, "type");
        usages.computeIfPresent(type, (t, counter) -> {
            long updated = counter.addAndGet(-Math.max(0L, amount));
            if (updated < 0L) {
                counter.set(0L);
            }
            return counter;
        });
    }

    public static class Builder {
        private final Map<ResourceType, QuotaLimit> limits = new ConcurrentHashMap<>();

        public Builder limit(ResourceType type, long maxAmount, String unit) {
            limits.put(type, QuotaLimit.of(type, maxAmount, unit));
            return this;
        }

        public Builder limit(QuotaLimit limit) {
            limits.put(limit.type(), limit);
            return this;
        }

        public DefaultResourceQuota build() {
            return new DefaultResourceQuota(limits);
        }
    }
}
