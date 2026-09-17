package tech.kayys.wayang.harness.resource;

import java.util.Objects;

/**
 * Current instantaneous or cumulative usage of a particular {@link ResourceType}.
 */
public record QuotaUsage(
        ResourceType type,
        long current,
        String unit
) {

    public QuotaUsage {
        Objects.requireNonNull(type, "type");
        unit = unit == null ? "units" : unit;
    }

    public static QuotaUsage of(ResourceType type, long current, String unit) {
        return new QuotaUsage(type, current, unit);
    }

    public static QuotaUsage zero(ResourceType type) {
        return new QuotaUsage(type, 0, "units");
    }
}
