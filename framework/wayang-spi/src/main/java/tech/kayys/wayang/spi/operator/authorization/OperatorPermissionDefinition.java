package tech.kayys.wayang.spi.operator.authorization;

/**
 * Definition of an operator permission's security attributes.
 */
public record OperatorPermissionDefinition(
        String id,
        String description,
        OperatorScope scope,
        boolean destructive,
        boolean privileged
) {
    public OperatorPermissionDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        if (scope == null) {
            throw new IllegalArgumentException("scope is required");
        }
    }
}
