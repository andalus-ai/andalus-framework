package tech.kayys.wayang.harness.authority;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a leadership term.
 */
public record LeadershipId(String value) {

    public LeadershipId {
        Objects.requireNonNull(value, "LeadershipId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("LeadershipId value cannot be blank");
        }
    }

    public static LeadershipId of(String value) {
        return new LeadershipId(value);
    }

    public static LeadershipId generate() {
        return new LeadershipId("lead-" + UUID.randomUUID());
    }
}
