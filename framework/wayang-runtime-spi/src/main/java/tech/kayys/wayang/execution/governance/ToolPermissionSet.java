package tech.kayys.wayang.execution.governance;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Immutable collection of fine-grained tool permission identifiers.
 */
public final class ToolPermissionSet {

    private final Set<String> ids;

    public ToolPermissionSet(Collection<String> permissions) {
        if (permissions == null) {
            this.ids = Set.of();
            return;
        }

        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String permission : permissions) {
            if (permission == null || permission.isBlank()) {
                continue;
            }
            normalized.add(permission.trim());
        }

        this.ids = Set.copyOf(normalized);
    }

    public static ToolPermissionSet empty() {
        return new ToolPermissionSet(Set.of());
    }

    public static ToolPermissionSet of(String... permissions) {
        if (permissions == null) {
            return empty();
        }
        return new ToolPermissionSet(Set.of(permissions));
    }

    public Set<String> ids() {
        return ids;
    }

    public boolean contains(String permissionId) {
        return permissionId != null && ids.contains(permissionId.trim());
    }

    public boolean isEmpty() {
        return ids.isEmpty();
    }

    public int size() {
        return ids.size();
    }
}
