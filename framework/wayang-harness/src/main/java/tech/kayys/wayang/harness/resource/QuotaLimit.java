package tech.kayys.wayang.harness.resource;

import java.util.Objects;

/**
 * Maximum capacity or boundary limit for a particular {@link ResourceType}.
 */
public record QuotaLimit(
        ResourceType type,
        long limit,
        String unit
) {

    public static final long UNLIMITED = Long.MAX_VALUE;

    public QuotaLimit {
        Objects.requireNonNull(type, "type");
        unit = unit == null ? "units" : unit;
    }

    public static QuotaLimit of(ResourceType type, long limit, String unit) {
        return new QuotaLimit(type, limit, unit);
    }

    public static QuotaLimit unlimited(ResourceType type) {
        return new QuotaLimit(type, UNLIMITED, "units");
    }

    public boolean isUnlimited() {
        return limit == UNLIMITED;
    }
}
