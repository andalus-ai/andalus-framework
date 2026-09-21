package tech.kayys.andalus.execution.conformance;

import java.util.Objects;

/**
 * Identifier for a published runtime conformance profile.
 */
public record ProfileId(String value) {

    public ProfileId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static ProfileId of(String value) {
        return new ProfileId(value);
    }
}
