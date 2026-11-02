package tech.kayys.andalus.execution.recovery;

/**
 * Runtime-specific port for restoring an execution from a validated checkpoint.
 *
 * <p>Implementations decode the provider-neutral checkpoint payload, rebuild the
 * runtime/workflow cursor and required state references, and return only after
 * the target runtime is ready to continue. Implementations must be idempotent
 * for the same execution and attempt because recovery may be retried after a
 * process crash.</p>
 */
@FunctionalInterface
public interface CheckpointRestorer {
    CheckpointRestorationResult restore(CheckpointRestorationRequest request);
}
