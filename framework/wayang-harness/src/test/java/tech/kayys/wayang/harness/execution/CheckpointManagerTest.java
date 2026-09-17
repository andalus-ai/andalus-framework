package tech.kayys.wayang.harness.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.context.DefaultHarnessContext;
import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.context.DefaultHarnessSession;
import tech.kayys.wayang.harness.execution.checkpoint.Checkpoint;
import tech.kayys.wayang.harness.execution.checkpoint.CheckpointId;
import tech.kayys.wayang.harness.execution.checkpoint.InMemoryCheckpointManager;
import tech.kayys.wayang.harness.execution.state.*;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CheckpointManagerTest {

    private InMemoryCheckpointManager manager;
    private DefaultExecutionState state;

    @BeforeEach
    void setUp() {
        manager = new InMemoryCheckpointManager();
        ExecutionId executionId = ExecutionId.of("exec-202");
        DefaultHarnessContext context = new DefaultHarnessContext(DefaultHarnessIdentity.of("agent-2"), DefaultHarnessSession.createNew(), Map.of());
        state = new DefaultExecutionState(executionId, ExecutionStatus.RUNNING, 1L, context, ExecutionCursor.initial(), ExecutionData.empty());
    }

    @Test
    void testCreateAndLoadCheckpoint() {
        CheckpointId cpId = manager.create(state);
        assertNotNull(cpId);

        Optional<Checkpoint> loaded = manager.load(cpId);
        assertTrue(loaded.isPresent());
        assertEquals(state.executionId(), loaded.get().executionId());
        assertEquals(1L, loaded.get().state().version());
    }

    @Test
    void testLatestCheckpoint() {
        CheckpointId cp1 = manager.create(state);
        DefaultExecutionState v2 = state.withStatus(ExecutionStatus.SUSPENDED);
        CheckpointId cp2 = manager.create(v2);

        Optional<Checkpoint> latest = manager.latest(state.executionId());
        assertTrue(latest.isPresent());
        assertEquals(cp2, latest.get().id());
        assertEquals(ExecutionStatus.SUSPENDED, latest.get().state().status());
    }

    @Test
    void testDeleteCheckpoint() {
        CheckpointId cpId = manager.create(state);
        assertTrue(manager.load(cpId).isPresent());

        manager.delete(cpId);
        assertTrue(manager.load(cpId).isEmpty());
        assertTrue(manager.latest(state.executionId()).isEmpty());
    }
}
