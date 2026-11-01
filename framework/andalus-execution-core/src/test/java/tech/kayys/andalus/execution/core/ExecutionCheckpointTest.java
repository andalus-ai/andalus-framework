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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

        CheckpointManifest staleManifest = new CheckpointManifest(
                "cp-bad", 1L, List.of(), List.of(), List.of(),
                CheckpointIntegrity.sha256(payload2), Instant.now(), Map.of());
        ExecutionCheckpoint badCp = new ExecutionCheckpoint(
                "cp-bad", execId, attId, 1L, payload2, staleManifest, Instant.now(), Map.of());
        assertThrows(IllegalArgumentException.class, () -> store.save(badCp));

        Optional<ExecutionCheckpoint> latest = store.getLatest(execId);
        assertTrue(latest.isPresent());
        assertEquals("cp-2", latest.get().checkpointId());
        assertEquals(2L, latest.get().sequence());
    }

    @Test
    void concurrentWritersCannotCommitTheSameSequence() throws Exception {
        InMemoryExecutionCheckpointStore store = new InMemoryExecutionCheckpointStore();
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<Boolean> first = executor.submit(() -> saveAfterBarrier(
                    store, checkpoint("concurrent-a", "exec-concurrent", 1L), ready, start));
            Future<Boolean> second = executor.submit(() -> saveAfterBarrier(
                    store, checkpoint("concurrent-b", "exec-concurrent", 1L), ready, start));

            ready.await();
            start.countDown();

            int accepted = (first.get() ? 1 : 0) + (second.get() ? 1 : 0);
            assertEquals(1, accepted, "exactly one writer may commit sequence 1");
            assertEquals(1, store.list("exec-concurrent").size());
            assertEquals(1L, store.getLatest("exec-concurrent").orElseThrow().sequence());
        }
    }

    @Test
    void checkpointIdsMustBeUniqueAcrossExecutions() {
        InMemoryExecutionCheckpointStore store = new InMemoryExecutionCheckpointStore();
        store.save(checkpoint("shared-id", "exec-one", 1L));

        assertThrows(IllegalArgumentException.class,
                () -> store.save(checkpoint("shared-id", "exec-two", 1L)));
        assertTrue(store.list("exec-two").isEmpty());
        assertEquals("exec-one", store.get("shared-id").orElseThrow().executionId());
    }


    @Test
    void checkpointPayloadIsDefensivelyCopiedOnConstructionAndAccess() {
        ExecutionCheckpoint checkpoint = checkpoint("immutable", "exec-immutable", 1L);
        byte[] exposed = checkpoint.payload();
        exposed[0] = 0;
        assertArrayEquals("payload-immutable".getBytes(StandardCharsets.UTF_8), checkpoint.payload());

        byte[] constructorPayload = "constructor-copy".getBytes(StandardCharsets.UTF_8);
        CheckpointManifest manifest = new CheckpointManifest(
                "constructor-copy", 1L, List.of(), List.of(), List.of(),
                CheckpointIntegrity.sha256(constructorPayload), Instant.now(), Map.of());
        ExecutionCheckpoint copied = new ExecutionCheckpoint(
                "constructor-copy", "exec-copy", AttemptId.random(), 1L,
                constructorPayload, manifest, Instant.now(), Map.of());
        constructorPayload[0] = 0;
        assertArrayEquals("constructor-copy".getBytes(StandardCharsets.UTF_8), copied.payload());
    }

    @Test
    void rejectsCheckpointWhenManifestIdentityOrIntegrityDoesNotMatch() {
        byte[] payload = "payload".getBytes(StandardCharsets.UTF_8);
        CheckpointManifest wrongId = new CheckpointManifest(
                "other-id", 1L, List.of(), List.of(), List.of(),
                CheckpointIntegrity.sha256(payload), Instant.now(), Map.of());
        assertThrows(IllegalArgumentException.class, () -> new ExecutionCheckpoint(
                "checkpoint-id", "execution-id", AttemptId.random(), 1L,
                payload, wrongId, Instant.now(), Map.of()));

        CheckpointManifest wrongSequence = new CheckpointManifest(
                "checkpoint-id", 2L, List.of(), List.of(), List.of(),
                CheckpointIntegrity.sha256(payload), Instant.now(), Map.of());
        assertThrows(IllegalArgumentException.class, () -> new ExecutionCheckpoint(
                "checkpoint-id", "execution-id", AttemptId.random(), 1L,
                payload, wrongSequence, Instant.now(), Map.of()));

        CheckpointManifest wrongChecksum = new CheckpointManifest(
                "checkpoint-id", 1L, List.of(), List.of(), List.of(),
                CheckpointIntegrity.sha256("different".getBytes(StandardCharsets.UTF_8)),
                Instant.now(), Map.of());
        assertThrows(IllegalArgumentException.class, () -> new ExecutionCheckpoint(
                "checkpoint-id", "execution-id", AttemptId.random(), 1L,
                payload, wrongChecksum, Instant.now(), Map.of()));
    }

    @Test
    void checkpointIntegrityRejectsUnsupportedAlgorithmAndMalformedChecksum() {
        assertThrows(IllegalArgumentException.class,
                () -> new CheckpointIntegrity("MD5", "00000000000000000000000000000000"));
        assertThrows(IllegalArgumentException.class,
                () -> new CheckpointIntegrity("SHA-256", "not-a-checksum"));
    }

    @Test
    void checkpointSequenceMustBePositive() {
        byte[] payload = "payload".getBytes(StandardCharsets.UTF_8);
        CheckpointManifest manifest = new CheckpointManifest(
                "zero", 0L, List.of(), List.of(), List.of(),
                CheckpointIntegrity.sha256(payload), Instant.now(), Map.of());
        assertThrows(IllegalArgumentException.class, () -> new ExecutionCheckpoint(
                "zero", "execution-id", AttemptId.random(), 0L,
                payload, manifest, Instant.now(), Map.of()));
    }

    private static boolean saveAfterBarrier(
            InMemoryExecutionCheckpointStore store,
            ExecutionCheckpoint checkpoint,
            CountDownLatch ready,
            CountDownLatch start) throws InterruptedException {
        ready.countDown();
        start.await();
        try {
            store.save(checkpoint);
            return true;
        } catch (IllegalArgumentException rejected) {
            return false;
        }
    }

    private static ExecutionCheckpoint checkpoint(String checkpointId, String executionId, long sequence) {
        byte[] payload = ("payload-" + checkpointId).getBytes(StandardCharsets.UTF_8);
        CheckpointIntegrity integrity = CheckpointIntegrity.sha256(payload);
        CheckpointManifest manifest = new CheckpointManifest(
                checkpointId,
                sequence,
                List.of(),
                List.of(),
                List.of(),
                integrity,
                Instant.now(),
                Map.of());
        return new ExecutionCheckpoint(
                checkpointId,
                executionId,
                AttemptId.random(),
                sequence,
                payload,
                manifest,
                Instant.now(),
                Map.of());
    }
}
