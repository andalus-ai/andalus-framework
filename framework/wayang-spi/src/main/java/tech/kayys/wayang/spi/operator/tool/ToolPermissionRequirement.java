package tech.kayys.wayang.spi.operator.tool;

public record ToolPermissionRequirement(
        String permissionId,
        boolean optional
) {
    public ToolPermissionRequirement {
        if (permissionId == null || permissionId.isBlank()) {
            throw new IllegalArgumentException("permissionId cannot be null or blank");
        }
        permissionId = permissionId.trim();
    }

    public static ToolPermissionRequirement required(String permissionId) {
        return new ToolPermissionRequirement(permissionId, false);
    }

    public static ToolPermissionRequirement optional(String permissionId) {
        return new ToolPermissionRequirement(permissionId, true);
    }
}
