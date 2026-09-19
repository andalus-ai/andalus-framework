package tech.kayys.wayang.state.core;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.state.SnapshotId;
import tech.kayys.wayang.state.StatePayload;
import tech.kayys.wayang.state.StateSnapshot;
import tech.kayys.wayang.state.StateVersion;
import tech.kayys.wayang.state.artifact.*;
import tech.kayys.wayang.state.checkpoint.*;
import tech.kayys.wayang.state.context.ContextSnapshot;
import tech.kayys.wayang.state.context.ContextSnapshotId;
import tech.kayys.wayang.state.context.ContextVersion;
import tech.kayys.wayang.state.handoff.HandoffId;
import tech.kayys.wayang.state.handoff.HandoffPackage;
import tech.kayys.wayang.state.provenance.ProvenanceId;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CheckpointAndHandoffTest {

    @Test
    void testCheckpointStoreAndHandoffPackage() {
        InMemoryCheckpointStore checkpointStore = new InMemoryCheckpointStore();

        StateSnapshot stateSnap = StateSnapshot.of(
                SnapshotId.random(),
                "exec-101",
                StateVersion.initial(),
                StatePayload.of("dummy-state".getBytes(), "text/plain"),
                Map.of()
        );

        ContextSnapshot ctxSnap = ContextSnapshot.of(
                ContextSnapshotId.random(),
                "sess-1",
                "task-1",
                List.of(),
                ContextVersion.initial()
        );

        ArtifactDigest digest = ArtifactDigest.sha256("artifact-data".getBytes());
        ArtifactReference artRef = new ArtifactReference(
                ArtifactId.random(),
                "report.txt",
                ArtifactType.REPORT,
                digest,
                ArtifactSize.of(13),
                new ArtifactLocation("artifact://sha256/" + digest.hexValue(), "memory"),
                Instant.now(),
                Map.of()
        );

        Checkpoint checkpoint = new Checkpoint(
                CheckpointId.random(),
                "exec-101",
                stateSnap,
                ctxSnap,
                List.of(artRef),
                ProvenanceId.random(),
                Instant.now(),
                Map.of()
        );

        checkpointStore.save(checkpoint);

        Optional<Checkpoint> retrieved = checkpointStore.get(checkpoint.id());
        assertTrue(retrieved.isPresent());
        assertEquals("exec-101", retrieved.get().executionId());
        assertEquals(1, retrieved.get().artifacts().size());

        // Test HandoffPackage creation
        HandoffPackage handoff = new HandoffPackage(
                HandoffId.random(),
                "task-1",
                "agent-alpha",
                "agent-beta",
                stateSnap,
                ctxSnap,
                List.of(artRef),
                List.of(),
                checkpoint.provenanceOffset(),
                Instant.now(),
                Map.of()
        );

        assertEquals("agent-alpha", handoff.sourceAgentId());
        assertEquals("agent-beta", handoff.targetAgentId());
        assertEquals("task-1", handoff.taskId());
    }
}
