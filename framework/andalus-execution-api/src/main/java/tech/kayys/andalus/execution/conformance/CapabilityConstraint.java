package tech.kayys.andalus.execution.conformance;

import java.util.Objects;

/**
 * Constraint or prerequisite under which a capability guarantee holds.
 */
public record CapabilityConstraint(String key, String expression) {

    public CapabilityConstraint {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(expression, "expression cannot be null");
    }

    public static CapabilityConstraint of(String expression) {
        Objects.requireNonNull(expression, "expression cannot be null");
        int eq = expression.indexOf('=');
        if (eq != -1) {
            return new CapabilityConstraint(expression.substring(0, eq).trim(), expression.substring(eq + 1).trim());
        }
        return new CapabilityConstraint("expr", expression.trim());
    }

    public static CapabilityConstraint of(String key, String expression) {
        return new CapabilityConstraint(key, expression);
    }
}
