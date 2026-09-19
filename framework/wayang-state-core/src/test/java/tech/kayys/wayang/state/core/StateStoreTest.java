package tech.kayys.wayang.state.core;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.state.SnapshotId;
import tech.kayys.wayang.state.StateKey;
import tech.kayys.wayang.state.StateSnapshot;
import tech.kayys.wayang.state.StateVersion;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StateStoreTest {

    record SampleState(String message, int count) implements Serializable {}

    @Test
    void testStateVersioningAndRetrieval() {
        InMemoryStateStore store = new InMemoryStateStore();
        StateKey key = StateKey.of("test-scope", "session-1");

        // Save v1
        StateSnapshot s1 = store.save(key, "exec-1", new SampleState("hello", 1));
        assertEquals(1L, s1.version().sequence());

        // Save v2
        StateSnapshot s2 = store.save(key, "exec-1", new SampleState("world", 2));
        assertEquals(2L, s2.version().sequence());

        // Verify latest
        Optional<StateSnapshot> latest = store.getLatest(key);
        assertTrue(latest.isPresent());
        assertEquals(2L, latest.get().version().sequence());

        // Load specific version
        Optional<SampleState> v1Loaded = store.load(key, new StateVersion(1L, "1.0"), SampleState.class);
        assertTrue(v1Loaded.isPresent());
        assertEquals("hello", v1Loaded.get().message());
        assertEquals(1, v1Loaded.get().count());

        Optional<SampleState> v2Loaded = store.load(key, new StateVersion(2L, "1.0"), SampleState.class);
        assertTrue(v2Loaded.isPresent());
        assertEquals("world", v2Loaded.get().message());
        assertEquals(2, v2Loaded.get().count());

        // History check
        List<StateSnapshot> history = store.getHistory(key);
        assertEquals(2, history.size());
    }
}
