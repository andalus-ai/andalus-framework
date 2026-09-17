package tech.kayys.wayang.execution.governance;

/**
 * Matcher for tool permissions supporting exact match, namespace wildcards (e.g. {@code filesystem.*}),
 * and global wildcards ({@code *}).
 */
public final class ToolPermissionMatcher {

    private ToolPermissionMatcher() {
    }

    public static boolean matches(String grantedPermission, String requestedPermission) {
        if (grantedPermission == null || requestedPermission == null) {
            return false;
        }

        String granted = grantedPermission.trim();
        String requested = requestedPermission.trim();

        if (granted.isEmpty() || requested.isEmpty()) {
            return false;
        }

        if ("*".equals(granted)) {
            return true;
        }

        if (granted.equals(requested)) {
            return true;
        }

        if (granted.endsWith(".*")) {
            String prefix = granted.substring(0, granted.length() - 1);
            return requested.startsWith(prefix);
        }

        return false;
    }
}
