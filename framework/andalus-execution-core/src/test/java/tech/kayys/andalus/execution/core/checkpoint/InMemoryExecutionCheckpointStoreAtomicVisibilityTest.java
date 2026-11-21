package tech.kayys.andalus.execution.core.checkpoint;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.checkpoint.CheckpointIntegrity;
import tech.kayys.andalus.execution.checkpoint.CheckpointManifest;
import tech.kayys.andalus.execution.checkpoint.ExecutionCheckpoint;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryExecutionCheckpointStoreAtomicVisibilityTest {

    @Test
    void pointLookupNeverExposesCheckpointBeforeHistory() throws Exception {
        InMemoryExecutionCheckpointStore store = new InMemoryExecutionCheckpointStore();
        String executionId = "atomic-visibility";
        AtomicBoolean writing = new AtomicBoolean(true);
        ExecutorService readers = Executors.newFixedThreadPool(4);

        List<Future<?>> observations = new java.util.ArrayList<>();
        for (int reader = 0; reader < 4; reader++) {
            observations.add(readers.submit(() -> {
                while (writing.get()) {
                    for (ExecutionCheckpoint checkpoint : store.list(executionId)) {
                        assertTrue(store.get(checkpoint.checkpointId()).isPresent());
                    }
                }
            }));
        }

        try {
            for (int sequence = 1; sequence <= 500; sequence++) {
                byte[] payload = ("payload-" + sequence).getBytes(StandardCharsets.UTF_8);
                String checkpointId = "checkpoint-" + sequence;
                Instant now = Instant.now();
                CheckpointManifest manifest = new CheckpointManifest(
                        checkpointId, sequence, List.of(), List.of(), List.of(),
                        CheckpointIntegrity.sha256(payload), now, Map.of());
                store.save(new ExecutionCheckpoint(
                        checkpointId, executionId, AttemptId.random(), sequence,
                        payload, manifest, now, Map.of()));
            }
        } finally {
            writing.set(false);
            readers.shutdown();
            assertTrue(readers.awaitTermination(10, TimeUnit.SECONDS));
        }

        for (Future<?> observation : observations) {
            observation.get();
        }
        assertEquals(500, store.list(executionId).size());
        assertEquals("checkpoint-500", store.getLatest(executionId).orElseThrow().checkpointId());
    }
}
