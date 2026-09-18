package tech.kayys.wayang.harness.workspace.v3;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.artifact.DiffArtifact;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.workspace.Workspace;
import tech.kayys.wayang.harness.workspace.WorkspaceId;
import tech.kayys.wayang.harness.workspace.WorkspacePolicy;
import tech.kayys.wayang.harness.workspace.WorkspaceRequest;
import tech.kayys.wayang.harness.workspace.WorkspaceType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WorkspaceLifecycleTest {

    @Test
    void testSnapshotManagement() {
        DefaultWorkspaceSnapshotManager manager = new DefaultWorkspaceSnapshotManager();
        WorkspaceId wsId = WorkspaceId.of("ws-proj-10");

        WorkspaceSnapshot snap = manager.create(wsId, new SnapshotOptions(false, false, "Baseline snapshot"));
        assertNotNull(snap.id());
        assertEquals(wsId, snap.workspaceId());

        Optional<WorkspaceSnapshot> found = manager.find(snap.id());
        assertTrue(found.isPresent());
        assertEquals("Baseline snapshot", found.get().metadata().get("note"));

        manager.delete(snap.id());
        assertTrue(manager.find(snap.id()).isEmpty());
    }

    @Test
    void testWorkspaceDiff() {
        DefaultWorkspaceDiffService diffService = new DefaultWorkspaceDiffService();
        ExecutionId execId = ExecutionId.of("exec-200");
        WorkspaceId wsId = WorkspaceId.of("ws-proj-10");

        DiffArtifact diff = diffService.diff(execId, wsId, new DiffBase.InitialState());
        assertNotNull(diff.id());
        assertEquals(DiffArtifact.DiffFormat.UNIFIED, diff.format());
        assertEquals(1, diff.changes().size());
        assertEquals("file.txt", diff.changes().iterator().next().path());
    }

    @Test
    void testTemporaryStorage() {
        TemporaryStorage tempStorage = TemporaryStorage.local();
        TempHandle handle = tempStorage.allocate("scratch-test");
        assertNotNull(handle.path());
        assertTrue(java.nio.file.Files.exists(handle.path()));

        tempStorage.release(handle);
        assertFalse(java.nio.file.Files.exists(handle.path()));
    }
}
