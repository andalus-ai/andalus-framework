package tech.kayys.wayang.execution.governance;

/**
 * Matcher for tool names supporting exact match, namespace wildcards (e.g. {@code filesystem.*}),
 * and global wildcards ({@code *}).
 */
public final class ToolNameMatcher {

    private ToolNameMatcher() {
    }

    public static boolean matches(String pattern, String toolName) {
        if (pattern == null || toolName == null) {
            return false;
        }

        String p = pattern.trim();
        String t = toolName.trim();

        if ("*".equals(p)) {
            return true;
        }

        if (p.equals(t)) {
            return true;
        }

        if (p.endsWith(".*")) {
            String prefix = p.substring(0, p.length() - 1);
            return t.startsWith(prefix);
        }

        return false;
    }
}
