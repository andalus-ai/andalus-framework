package tech.kayys.wayang.execution.secrets;

import java.util.Objects;

/**
 * Reference to a secret without exposing the underlying secret material.
 */
public record SecretReference(
        SecretId id,
        SecretAccessMode mode
) {

    public SecretReference {
        Objects.requireNonNull(id, "SecretId cannot be null");
        mode = mode != null ? mode : SecretAccessMode.EPHEMERAL;
    }

    public static SecretReference of(SecretId id, SecretAccessMode mode) {
        return new SecretReference(id, mode);
    }
}
