package tech.kayys.wayang.execution.checkpoint;

import java.util.List;
import java.util.Optional;

public interface CheckpointStore {
    void save(ExecutionCheckpoint checkpoint);
    Optional<ExecutionCheckpoint> get(String checkpointId);
    Optional<ExecutionCheckpoint> getLatest(String executionId);
    List<ExecutionCheckpoint> list(String executionId);
}
