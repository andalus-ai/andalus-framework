package tech.kayys.wayang.state;

import java.util.Objects;

/**
 * Version of a state instance, combining sequence number and schema version.
 */
public record StateVersion(long sequence, String schemaVersion) {
    public StateVersion {
        if (sequence < 0) {
            throw new IllegalArgumentException("sequence must be >= 0");
        }
        Objects.requireNonNull(schemaVersion, "schemaVersion cannot be null");
    }

    public static StateVersion initial() {
        return new StateVersion(1L, "1.0");
    }

    public StateVersion next() {
        return new StateVersion(sequence + 1, schemaVersion);
    }
}
