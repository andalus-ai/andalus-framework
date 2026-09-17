package tech.kayys.wayang.execution.governance;

/**
 * Declares a fine-grained permission that an actor or tool operation requires.
 *
 * <p>Permission IDs follow the format {@code <domain>.<operation>}, e.g.
 * {@code filesystem.read}, {@code filesystem.write}, {@code process.execute}.</p>
 */
public record ToolPermission(
        String id,
        String description
) {

    public ToolPermission {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Permission id cannot be null or blank");
        }

        id = id.trim();
        description = description == null ? "" : description.trim();
    }

    public static ToolPermission of(String id) {
        return new ToolPermission(id, "");
    }

    public static ToolPermission of(String id, String description) {
        return new ToolPermission(id, description);
    }
}
