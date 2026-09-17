package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ToolPermissionMatcherTest {

    @Test
    void exactMatch() {
        assertTrue(ToolPermissionMatcher.matches("filesystem.read", "filesystem.read"));
    }

    @Test
    void exactMismatch() {
        assertFalse(ToolPermissionMatcher.matches("filesystem.read", "filesystem.write"));
    }

    @Test
    void namespaceWildcard() {
        assertTrue(ToolPermissionMatcher.matches("filesystem.*", "filesystem.read"));
        assertTrue(ToolPermissionMatcher.matches("filesystem.*", "filesystem.write"));
        assertTrue(ToolPermissionMatcher.matches("filesystem.*", "filesystem.delete"));
    }

    @Test
    void namespaceWildcardDoesNotCrossNamespace() {
        assertFalse(ToolPermissionMatcher.matches("filesystem.*", "network.connect"));
    }

    @Test
    void globalWildcard() {
        assertTrue(ToolPermissionMatcher.matches("*", "filesystem.read"));
        assertTrue(ToolPermissionMatcher.matches("*", "process.execute"));
        assertTrue(ToolPermissionMatcher.matches("*", "any.permission"));
    }

    @Test
    void nullReturnsFalse() {
        assertFalse(ToolPermissionMatcher.matches(null, "filesystem.read"));
        assertFalse(ToolPermissionMatcher.matches("filesystem.read", null));
        assertFalse(ToolPermissionMatcher.matches(null, null));
    }

    @Test
    void blankReturnsFalse() {
        assertFalse(ToolPermissionMatcher.matches("", "filesystem.read"));
        assertFalse(ToolPermissionMatcher.matches("filesystem.read", ""));
    }
}
