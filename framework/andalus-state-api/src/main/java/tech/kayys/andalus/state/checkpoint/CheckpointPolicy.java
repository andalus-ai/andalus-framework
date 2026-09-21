package tech.kayys.andalus.state.checkpoint;

import tech.kayys.andalus.state.StateSnapshot;

public interface CheckpointPolicy {
    boolean shouldCheckpoint(String eventType, StateSnapshot state);
}
