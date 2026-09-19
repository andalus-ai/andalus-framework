package tech.kayys.wayang.state.context;

import java.util.List;
import java.util.Optional;

public interface ContextSnapshotStore {
    void save(ContextSnapshot snapshot);
    Optional<ContextSnapshot> get(ContextSnapshotId id);
    Optional<ContextSnapshot> getLatestForTask(String taskId);
    List<ContextSnapshot> listForSession(String sessionId);
}
