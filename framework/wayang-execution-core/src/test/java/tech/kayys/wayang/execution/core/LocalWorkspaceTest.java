package tech.kayys.wayang.execution.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tech.kayys.wayang.execution.core.workspace.LocalWorkspace;
import tech.kayys.wayang.execution.workspace.WorkspaceId;
import tech.kayys.wayang.execution.workspace.WorkspaceMode;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocalWorkspaceTest {

    @TempDir
    Path tempDir;

    private LocalWorkspace workspace;

    @BeforeEach
    void setUp() {
        workspace = new LocalWorkspace(WorkspaceId.of("ws-test"), WorkspaceMode.EPHEMERAL, tempDir.resolve("sub-ws"));
    }

    @AfterEach
    void tearDown() throws Exception {
        workspace.close();
    }

    @Test
    void testBasicWriteAndRead() throws Exception {
        try (OutputStream out = workspace.write("test.txt")) {
            out.write("Hello Execution Sandbox".getBytes(StandardCharsets.UTF_8));
        }

        assertTrue(workspace.exists("test.txt"));

        try (InputStream in = workspace.read("test.txt")) {
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals("Hello Execution Sandbox", content);
        }

        List<String> files = workspace.list(".");
        assertEquals(1, files.size());
        assertEquals("test.txt", files.getFirst());
    }

    @Test
    void testPathTraversalBlocked() {
        assertThrows(IllegalArgumentException.class, () -> workspace.write("../escape.txt"));
        assertThrows(IllegalArgumentException.class, () -> workspace.read("../../etc/passwd"));
    }

    @Test
    void testReadOnlyMode() throws Exception {
        LocalWorkspace roWs = new LocalWorkspace(WorkspaceId.of("ws-ro"), WorkspaceMode.READ_ONLY, tempDir.resolve("ro-ws"));
        assertThrows(IllegalStateException.class, () -> roWs.write("forbidden.txt"));
        roWs.close();
    }
}
