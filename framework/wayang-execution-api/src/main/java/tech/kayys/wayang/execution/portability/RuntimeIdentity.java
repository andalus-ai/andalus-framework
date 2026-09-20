package tech.kayys.wayang.execution.portability;

import java.util.Objects;

/**
 * Normalized identity of a runtime environment.
 */
public record RuntimeIdentity(String providerId, String runtimeType, String version) {

    public RuntimeIdentity {
        Objects.requireNonNull(providerId, "providerId cannot be null");
        Objects.requireNonNull(runtimeType, "runtimeType cannot be null");
        Objects.requireNonNull(version, "version cannot be null");
    }

    public static RuntimeIdentity of(String providerId, String runtimeType, String version) {
        return new RuntimeIdentity(providerId, runtimeType, version);
    }
}
