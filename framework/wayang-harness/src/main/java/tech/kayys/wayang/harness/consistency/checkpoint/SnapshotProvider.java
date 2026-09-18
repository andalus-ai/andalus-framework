package tech.kayys.wayang.harness.consistency.checkpoint;

/**
 * Contract implemented by stateful subsystems that participate in checkpointing.
 *
 * @param <T> the snapshot representation type
 */
public interface SnapshotProvider<T> {

    T snapshot();

    boolean restore(T snapshot);
}
