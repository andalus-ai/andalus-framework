package tech.kayys.wayang.harness.workspace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkspacePathTest {

    @Test
    void testNormalPathAndRoot() {
        WorkspacePath root = WorkspacePath.root();
        assertTrue(root.isRoot());
        assertEquals("", root.value());

        WorkspacePath path = WorkspacePath.of("src/main/java/App.java");
        assertEquals("src/main/java/App.java", path.value());
        assertFalse(path.isRoot());

        WorkspacePath leadingSlash = WorkspacePath.of("/src/main/java/App.java");
        assertEquals("src/main/java/App.java", leadingSlash.value());

        WorkspacePath windowsBackslashes = WorkspacePath.of("src\\main\\java\\App.java");
        assertEquals("src/main/java/App.java", windowsBackslashes.value());
    }

    @Test
    void testResolve() {
        WorkspacePath root = WorkspacePath.root();
        WorkspacePath resolved = root.resolve("src/index.ts");
        assertEquals("src/index.ts", resolved.value());

        WorkspacePath child = resolved.resolve("nested/file.txt");
        assertEquals("src/index.ts/nested/file.txt", child.value());
    }

    @Test
    void testForbiddenPathTraversal() {
        assertThrows(IllegalArgumentException.class, () -> WorkspacePath.of(".."));
        assertThrows(IllegalArgumentException.class, () -> WorkspacePath.of("../etc/passwd"));
        assertThrows(IllegalArgumentException.class, () -> WorkspacePath.of("src/../../secret"));
        assertThrows(IllegalArgumentException.class, () -> WorkspacePath.root().resolve("../escape"));
    }
}
