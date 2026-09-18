package tech.kayys.wayang.harness.environment.v3.secret;

import java.util.Objects;

/**
 * Logical reference to a secret key or credential.
 */
public record SecretRef(String name, String scope) {
    public SecretRef {
        Objects.requireNonNull(name, "name");
        scope = scope != null ? scope : "default";
    }

    public static SecretRef of(String name) {
        return new SecretRef(name, "default");
    }

    public static SecretRef of(String name, String scope) {
        return new SecretRef(name, scope);
    }
}
