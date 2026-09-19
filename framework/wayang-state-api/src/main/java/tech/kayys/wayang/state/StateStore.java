package tech.kayys.wayang.state;

import java.util.List;
import java.util.Optional;

/**
 * SPI for persisting and querying state snapshots.
 */
public interface StateStore {
    <T> StateSnapshot save(StateKey key, String executionId, T state);
    <T> Optional<T> load(StateKey key, StateVersion version, Class<T> type);
    Optional<StateSnapshot> getLatest(StateKey key);
    List<StateSnapshot> getHistory(StateKey key);
}
