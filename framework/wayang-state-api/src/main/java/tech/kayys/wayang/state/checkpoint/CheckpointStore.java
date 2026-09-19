package tech.kayys.wayang.state.checkpoint;

import java.util.List;
import java.util.Optional;

public interface CheckpointStore {
    void save(Checkpoint checkpoint);
    Optional<Checkpoint> get(CheckpointId id);
    Optional<Checkpoint> getLatest(String executionId);
    List<Checkpoint> list(String executionId);
}
