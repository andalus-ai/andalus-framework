package tech.kayys.wayang.execution.secrets;

import java.util.Objects;

/**
 * Identifier for a confidential secret reference.
 */
public record SecretId(String value) {

    public SecretId {
        Objects.requireNonNull(value, "SecretId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("SecretId cannot be blank");
        }
        value = value.trim();
    }

    public static SecretId of(String value) {
        return new SecretId(value);
    }
}
