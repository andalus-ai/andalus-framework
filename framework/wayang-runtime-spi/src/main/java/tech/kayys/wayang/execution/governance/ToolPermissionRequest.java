package tech.kayys.wayang.execution.governance;

/**
 * Represents a runtime permission request for a specific operation, optionally scoped to a target resource.
 */
public record ToolPermissionRequest(
        String permissionId,
        String resource
) {

    public ToolPermissionRequest {
        if (permissionId == null || permissionId.isBlank()) {
            throw new IllegalArgumentException("permissionId cannot be null or blank");
        }

        permissionId = permissionId.trim();

        if (resource != null) {
            resource = resource.trim();
            if (resource.isBlank()) {
                resource = null;
            }
        }
    }

    public static ToolPermissionRequest of(String permissionId) {
        return new ToolPermissionRequest(permissionId, null);
    }

    public static ToolPermissionRequest of(String permissionId, String resource) {
        return new ToolPermissionRequest(permissionId, resource);
    }
}
