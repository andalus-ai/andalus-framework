package tech.kayys.andalus.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.checkpoint.CheckpointIntegrity;
import tech.kayys.andalus.execution.checkpoint.CheckpointManifest;
import tech.kayys.andalus.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.andalus.execution.core.checkpoint.InMemoryExecutionCheckpointStore;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutionCheckpointTest {

    @Test
    void testCheckpointSequencingAndIntegrityVerification() {
        InMemoryExecutionCheckpointStore store = new InMemoryExecutionCheckpointStore();
        String execId = "exec-500";
        AttemptId attId = AttemptId.random();

        byte[] payload1 = "state-v1".getBytes(StandardCharsets.UTF_8);
        CheckpointIntegrity integrity1 = CheckpointIntegrity.sha256(payload1);
        assertTrue(integrity1.verify(payload1));
        assertFalse(integrity1.verify("tampered".getBytes(StandardCharsets.UTF_8)));

        CheckpointManifest manifest1 = new CheckpointManifest(
                "cp-1",
                1L,
                List.of("state:task"),
                List.of("artifact:123"),
                List.of(),
                integrity1,
                Instant.now(),
                Map.of()
        );

        ExecutionCheckpoint cp1 = new ExecutionCheckpoint(
                "cp-1",
                execId,
                attId,
                1L,
                payload1,
                manifest1,
                Instant.now(),
                Map.of()
        );

        store.save(cp1);

        // Sequence monotonicity check
        byte[] payload2 = "state-v2".getBytes(StandardCharsets.UTF_8);
        CheckpointManifest manifest2 = new CheckpointManifest(
                "cp-2",
                2L,
                List.of("state:task"),
                List.of("artifact:123"),
                List.of(),
                CheckpointIntegrity.sha256(payload2),
                Instant.now(),
                Map.of()
        );
        ExecutionCheckpoint cp2 = new ExecutionCheckpoint("cp-2", execId, attId, 2L, payload2, manifest2, Instant.now(), Map.of());
        store.save(cp2);

        // Illegal non-monotonic checkpoint save must throw IllegalArgumentException
        ExecutionCheckpoint badCp = new ExecutionCheckpoint("cp-bad", execId, attId, 1L, payload2, manifest2, Instant.now(), Map.of());
        assertThrows(IllegalArgumentException.class, () -> store.save(badCp));

        // Verification of latest
        Optional<ExecutionCheckpoint> latest = store.getLatest(execId);
        assertTrue(latest.isPresent());
        assertEquals("cp-2", latest.get().checkpointId());
        assertEquals(2L, latest.get().sequence());
    }
}
