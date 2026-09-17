package tech.kayys.wayang.harness.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.context.DefaultHarnessContext;
import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.context.DefaultHarnessSession;
import tech.kayys.wayang.harness.execution.state.*;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionStateStoreTest {

    private InMemoryExecutionStateStore store;
    private ExecutionId executionId;
    private DefaultHarnessContext context;

    @BeforeEach
    void setUp() {
        store = new InMemoryExecutionStateStore();
        executionId = ExecutionId.of("exec-101");
        context = new DefaultHarnessContext(DefaultHarnessIdentity.of("agent-1"), DefaultHarnessSession.createNew(), Map.of());
    }

    @Test
    void testSaveAndLoadInitialState() {
        DefaultExecutionState state = new DefaultExecutionState(executionId, ExecutionStatus.CREATED, 0L, context, ExecutionCursor.initial(), ExecutionData.empty());
        store.save(state, 0L);

        Optional<ExecutionState> loaded = store.load(executionId);
        assertTrue(loaded.isPresent());
        assertEquals(0L, loaded.get().version());
        assertEquals(ExecutionStatus.CREATED, loaded.get().status());
    }

    @Test
    void testOptimisticConcurrencySuccess() {
        DefaultExecutionState v0 = new DefaultExecutionState(executionId, ExecutionStatus.CREATED, 0L, context, ExecutionCursor.initial(), ExecutionData.empty());
        store.save(v0, 0L);

        DefaultExecutionState v1 = v0.withStatus(ExecutionStatus.RUNNING);
        store.save(v1, 0L);

        Optional<ExecutionState> loaded = store.load(executionId);
        assertTrue(loaded.isPresent());
        assertEquals(1L, loaded.get().version());
        assertEquals(ExecutionStatus.RUNNING, loaded.get().status());
    }

    @Test
    void testOptimisticConcurrencyConflict() {
        DefaultExecutionState v0 = new DefaultExecutionState(executionId, ExecutionStatus.CREATED, 0L, context, ExecutionCursor.initial(), ExecutionData.empty());
        store.save(v0, 0L);

        DefaultExecutionState v1 = v0.withStatus(ExecutionStatus.RUNNING);
        store.save(v1, 0L);

        // Attempting to update expecting version 0 when store has version 1 must throw
        DefaultExecutionState conflicting = v0.withStatus(ExecutionStatus.SUSPENDED);
        assertThrows(ConcurrentModificationException.class, () -> store.save(conflicting, 0L));
    }
}
