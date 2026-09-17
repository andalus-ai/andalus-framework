package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ToolPermissionTest {

    @Test
    void ofCreatesWithEmptyDescription() {
        ToolPermission p = ToolPermission.of("filesystem.read");
        assertEquals("filesystem.read", p.id());
        assertEquals("", p.description());
    }

    @Test
    void trimmedOnConstruction() {
        ToolPermission p = ToolPermission.of("  filesystem.write  ", "  Write files  ");
        assertEquals("filesystem.write", p.id());
        assertEquals("Write files", p.description());
    }

    @Test
    void blankIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> ToolPermission.of("  "));
    }

    @Test
    void nullIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> ToolPermission.of(null));
    }
}
