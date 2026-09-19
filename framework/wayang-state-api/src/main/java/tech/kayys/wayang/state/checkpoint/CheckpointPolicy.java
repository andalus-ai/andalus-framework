package tech.kayys.wayang.state.checkpoint;

import tech.kayys.wayang.state.StateSnapshot;

public interface CheckpointPolicy {
    boolean shouldCheckpoint(String eventType, StateSnapshot state);
}
