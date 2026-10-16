package tech.kayys.andalus.a2a.durable;

import tech.kayys.andalus.a2a.model.A2AArtifact;

import java.util.List;
import java.util.Map;

/**
 * Bridges terminal A2A task results to the local durable execution (6.16.5.5).
 *
 * <p>Contract:</p>
 * <ul>
 *   <li>The task's terminal state (and result envelope) is persisted
 *       <em>before</em> the local execution is woken (6.16.5.14).</li>
 *   <li>Duplicate terminal signals are safe: a task already in a terminal state
 *       is not transitioned again, and a duplicated wake is harmless (6.16.5.16).</li>
 *   <li>Task completion is <em>not</em> execution completion — the woken execution
 *       inspects the persisted outcome and decides (6.16.5.4).</li>
 * </ul>
 *
 * <p>This bridge does not own execution state, retry, compensation, or recovery.</p>
 */
public interface A2ACompletionBridge {

    /** Persists COMPLETED and wakes the caller execution. */
    void completed(String taskId);

    /**
     * Persists COMPLETED together with the protocol-visible result
     * (artifacts + metadata) and wakes the caller execution (6.16.5.13).
     */
    void completed(String taskId, List<A2AArtifact> artifacts, Map<String, Object> metadata);

    /** Persists FAILED with the remote reason and wakes the caller execution. */
    void failed(String taskId, String reason);

    /** Persists CANCELLED and wakes the caller execution. */
    void cancelled(String taskId);
}
