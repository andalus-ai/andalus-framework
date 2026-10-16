package tech.kayys.andalus.harness.coordination;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.util.Optional;

/**
 * Integration SPI persisting the Gamelan-dispatch ↔ Claw-execution binding
 * (6.17.2.14 / 6.17.2.31).
 *
 * <p>This store deliberately lives outside {@code ExecutionStateStore}: Claw's canonical
 * state API stays Gamelan-agnostic, and the integration joins the two identities
 * (6.17.2.15). The SPI does not dictate the persistence technology — an in-memory
 * implementation serves standalone/community deployments, a relational one can later
 * share Gamelan's database (6.17.2.32).</p>
 *
 * <p><strong>Invariants (6.17.2.16):</strong></p>
 * <pre>
 *   dispatchId  → at most one active ExecutionId
 *   ExecutionId → at most one active dispatch
 * </pre>
 *
 * <p>No distributed transaction is introduced: the local durable operation happens
 * first, the binding follows, and idempotent reconciliation closes the gap
 * (6.17.2.33).</p>
 */
public interface GamelanExecutionBindingStore {

    /** Looks up a binding by its derived Gamelan dispatch identity. */
    Optional<AgentExecutionBinding> find(String tenantId, String workflowRunId,
                                         String nodeId, int attempt);

    /** Looks up the binding owning a Claw execution (used by wake/resume — 6.17.11). */
    Optional<AgentExecutionBinding> findByExecutionId(ExecutionId executionId);

    /** Looks up the active binding of a node across its attempts, if any. */
    Optional<AgentExecutionBinding> findActiveForNode(String tenantId, String workflowRunId,
                                                      String nodeId);

    /**
     * Persists a binding.
     *
     * <p>Idempotent for the same {@code (dispatchId, executionId)} pair; throws
     * {@link BindingConflictException} when the dispatch is already bound to a
     * <em>different</em> execution (6.17.2.17). A new Gamelan node attempt creates a
     * distinct dispatch and therefore supersedes (but does not mutate) the previous
     * attempt's binding (6.17.2.21).</p>
     *
     * @return the persisted binding (the existing one when the bind was idempotent)
     */
    AgentExecutionBinding bind(AgentExecutionBinding binding);

    /** Marks the binding of this execution as completed, retaining the record (6.17.2.23). */
    void complete(ExecutionId executionId);

    /** Removes a binding (used only when a dispatch is abandoned, never on completion). */
    void remove(AgentExecutionBinding binding);
}
