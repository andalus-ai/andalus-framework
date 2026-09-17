package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ToolPermissionSetTest {

    @Test
    void ofCreatesWithPermissions() {
        ToolPermissionSet set = ToolPermissionSet.of("filesystem.read", "filesystem.write");
        assertEquals(2, set.size());
        assertTrue(set.contains("filesystem.read"));
        assertTrue(set.contains("filesystem.write"));
    }

    @Test
    void emptySet() {
        ToolPermissionSet set = ToolPermissionSet.empty();
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
    }

    @Test
    void nullAndBlankValuesFiltered() {
        ToolPermissionSet set = new ToolPermissionSet(java.util.List.of("filesystem.read", "  ", "process.execute"));
        assertEquals(2, set.size());
        assertTrue(set.contains("filesystem.read"));
        assertTrue(set.contains("process.execute"));
    }

    @Test
    void containsTrimsPermissionId() {
        ToolPermissionSet set = ToolPermissionSet.of("filesystem.read");
        assertTrue(set.contains("  filesystem.read  "));
    }

    @Test
    void nullConstructorCreatesEmpty() {
        ToolPermissionSet set = new ToolPermissionSet(null);
        assertTrue(set.isEmpty());
    }
}
