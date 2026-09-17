package tech.kayys.wayang.harness.workspace;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocalWorkspaceTest {

    @TempDir
    Path tempDir;
    LocalWorkspace workspace;

    @BeforeEach
    void setUp() {
        workspace = new LocalWorkspace(
                WorkspaceId.of("test-ws"),
                WorkspaceType.LOCAL,
                WorkspacePolicy.readWrite(),
                tempDir.resolve("workspace"),
                false
        );
    }

    @AfterEach
    void tearDown() {
        workspace.release();
    }

    @Test
    void testWorkspaceId() {
        assertEquals("test-ws", workspace.id().value());
        assertEquals(WorkspaceType.LOCAL, workspace.type());
    }

    @Test
    void testWriteAndRead() throws Exception {
        WorkspacePath path = WorkspacePath.of("hello.txt");
        try (OutputStream out = workspace.write(path)) {
            out.write("Hello Wayang!".getBytes(StandardCharsets.UTF_8));
        }
        assertTrue(workspace.exists(path));
        byte[] bytes = workspace.read(path).readAllBytes();
        assertEquals("Hello Wayang!", new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    void testListFiles() throws Exception {
        try (OutputStream out = workspace.write(WorkspacePath.of("a.txt"))) {
            out.write("a".getBytes());
        }
        try (OutputStream out = workspace.write(WorkspacePath.of("b.txt"))) {
            out.write("b".getBytes());
        }
        List<WorkspacePath> files = workspace.list(WorkspacePath.root());
        assertEquals(2, files.size());
    }

    @Test
    void testPathTraversalBlocked() {
        assertThrows(IllegalArgumentException.class, () ->
                workspace.read(WorkspacePath.of("../etc/passwd"))
        );
    }

    @Test
    void testReadOnlyPolicyBlocks() {
        LocalWorkspace roWorkspace = new LocalWorkspace(
                WorkspaceId.of("ro-ws"),
                WorkspaceType.LOCAL,
                WorkspacePolicy.readOnlyPolicy(),
                tempDir.resolve("ro-workspace"),
                false
        );
        try {
            assertThrows(SecurityException.class, () ->
                    roWorkspace.write(WorkspacePath.of("file.txt"))
            );
        } finally {
            roWorkspace.release();
        }
    }

    @Test
    void testSnapshotCapture() throws Exception {
        try (OutputStream out = workspace.write(WorkspacePath.of("snap.txt"))) {
            out.write("data".getBytes());
        }
        WorkspaceSnapshot snap = workspace.snapshot();
        assertNotNull(snap);
        assertTrue(snap.fileCount() > 0);
        assertTrue(snap.totalBytes() > 0);
    }

    @Test
    void testReleaseBlocksSubsequentAccess() {
        workspace.release();
        assertThrows(IllegalStateException.class, () -> workspace.snapshot());
    }
}
