package tech.kayys.andalus.harness.coordination;

import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionState;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Default {@link GamelanClawBridge} (6.17), backed by a
 * {@link GamelanExecutionBindingStore}.
 *
 * <p>Responsibilities (deliberately narrow — 6.17.2.29):</p>
 * <pre>
 *   correlation + binding + translation + reconciliation
 * </pre>
 *
 * <p>It is <em>not</em> a workflow engine, execution engine, retry manager, checkpoint
 * manager, or state model. All Gamelan state changes go through Gamelan's own commit
 * services via the {@code notifyGamelan*} extension points (6.17.2.11).</p>
 *
 * <p>Corrections over the previous in-memory implementation:</p>
 * <ul>
 *   <li>Keying is tenant-scoped — previously {@code run:node} could collide across
 *       tenants (6.17.2.19).</li>
 *   <li>Dispatch identity is {@code tenant + run + node + attempt} (6.17.3.31), so a
 *       Gamelan node retry is a <em>distinct</em> dispatch rather than a silent rebind
 *       (6.17.2.21).</li>
 *   <li>Conflicting rebinds raise {@link BindingConflictException} instead of being
 *       silently absorbed (6.17.2.17).</li>
 *   <li>Completion retains the binding and stale results are rejected rather than
 *       re-notified (6.17.2.23, 6.17.11).</li>
 * </ul>
 */
public class DefaultGamelanClawBridge implements GamelanClawBridge {

    private static final Logger LOG = Logger.getLogger(DefaultGamelanClawBridge.class.getName());

    private final GamelanExecutionBindingStore store;

    public DefaultGamelanClawBridge() {
        this(new InMemoryGamelanExecutionBindingStore());
    }

    public DefaultGamelanClawBridge(GamelanExecutionBindingStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    public GamelanExecutionBindingStore store() {
        return store;
    }

    // ── Dispatch / binding ────────────────────────────────────────────────────

    @Override
    public AgentExecutionBinding bindNodeExecution(String tenantId, String workflowRunId,
                                                    String nodeId, String reservationId) {
        return bindNodeExecution(tenantId, workflowRunId, nodeId, 1, reservationId);
    }

    @Override
    public AgentExecutionBinding bindNodeExecution(String tenantId, String workflowRunId,
                                                    String nodeId, int attempt,
                                                    String reservationId) {
        Objects.requireNonNull(tenantId,      "tenantId");
        Objects.requireNonNull(workflowRunId, "workflowRunId");
        Objects.requireNonNull(nodeId,        "nodeId");

        // Idempotent replay of the same dispatch returns the existing binding.
        Optional<AgentExecutionBinding> existing =
                store.find(tenantId, workflowRunId, nodeId, attempt);
        if (existing.isPresent()) {
            AgentExecutionBinding binding = existing.get();
            LOG.fine(() -> "Idempotent bind: dispatch=" + binding.dispatchId()
                    + " already bound to exec=" + binding.executionId().value());
            return binding;
        }

        // A new attempt supersedes the previous attempt's binding; that binding is
        // retained (never mutated) so late results stay attributable (6.17.2.21).
        store.findActiveForNode(tenantId, workflowRunId, nodeId).ifPresent(previous ->
                LOG.info(() -> "Gamelan node " + nodeId + " re-dispatched (attempt " + attempt
                        + "); previous exec=" + previous.executionId().value() + " superseded"));

        // Never equate WorkflowRunId/NodeId with ExecutionId (6.17.3).
        ExecutionId executionId = ExecutionId.of("exec-" + UUID.randomUUID());
        AgentExecutionBinding binding = AgentExecutionBinding.of(
                tenantId, workflowRunId, nodeId, attempt, executionId, reservationId);

        AgentExecutionBinding stored = store.bind(binding);
        LOG.info(() -> "Bound dispatch=" + stored.dispatchId()
                + " -> exec=" + stored.executionId().value() + " (reservation=" + reservationId + ")");
        return stored;
    }

    // ── Completion notifications ───────────────────────────────────────────────

    @Override
    public void onExecutionCompleted(AgentExecutionBinding binding, ExecutionState finalState) {
        Objects.requireNonNull(binding, "binding");
        if (!isCurrentDispatch(binding)) {
            LOG.warning(() -> "Stale completion ignored: exec=" + binding.executionId().value()
                    + " is no longer the active dispatch for node=" + binding.nodeId());
            return;
        }
        store.complete(binding.executionId());
        LOG.info(() -> "Gamelan notification: node=" + binding.nodeId()
                + " COMPLETED in run=" + binding.workflowRunId()
                + " (exec=" + binding.executionId().value() + ")");
        notifyGamelanNodeCompleted(binding, finalState);
    }

    @Override
    public void onExecutionFailed(AgentExecutionBinding binding, String reason) {
        Objects.requireNonNull(binding, "binding");
        if (!isCurrentDispatch(binding)) {
            LOG.warning(() -> "Stale failure ignored: exec=" + binding.executionId().value()
                    + " is no longer the active dispatch for node=" + binding.nodeId());
            return;
        }
        store.complete(binding.executionId());
        LOG.warning(() -> "Gamelan notification: node=" + binding.nodeId()
                + " FAILED in run=" + binding.workflowRunId() + ": " + reason);
        notifyGamelanNodeFailed(binding, reason);
    }

    @Override
    public void onExecutionCancelled(AgentExecutionBinding binding) {
        Objects.requireNonNull(binding, "binding");
        if (!isCurrentDispatch(binding)) {
            LOG.warning(() -> "Stale cancellation ignored: exec=" + binding.executionId().value()
                    + " is no longer the active dispatch for node=" + binding.nodeId());
            return;
        }
        store.complete(binding.executionId());
        LOG.info(() -> "Gamelan notification: node=" + binding.nodeId()
                + " CANCELLED in run=" + binding.workflowRunId());
        notifyGamelanNodeCancelled(binding);
    }

    // ── Reconciliation ────────────────────────────────────────────────────────

    @Override
    public Optional<AgentExecutionBinding> reconcileNode(String tenantId, String workflowRunId,
                                                        String nodeId) {
        Optional<AgentExecutionBinding> binding =
                store.findActiveForNode(tenantId, workflowRunId, nodeId);
        if (binding.isEmpty()) {
            LOG.warning(() -> "reconcile: no active binding for node=" + nodeId
                    + " in run=" + workflowRunId + " (tenant=" + tenantId + ")");
        }
        return binding;
    }

    @Override
    public Optional<AgentExecutionBinding> findByExecutionId(ExecutionId executionId) {
        return store.findByExecutionId(executionId);
    }

    // ── Extension points for the Gamelan-side adapter ─────────────────────────

    /**
     * Calls Gamelan's authoritative completion boundary.
     * Override to call {@code WorkflowRunManager.handleNodeResult(...)} (6.17.4).
     */
    protected void notifyGamelanNodeCompleted(AgentExecutionBinding binding, ExecutionState finalState) {
        // no-op stub — the Gamelan adapter overrides this
    }

    /** Override to report node failure to Gamelan (Gamelan decides retry/compensation). */
    protected void notifyGamelanNodeFailed(AgentExecutionBinding binding, String reason) {
        // no-op stub — the Gamelan adapter overrides this
    }

    /** Override to report node cancellation to Gamelan. */
    protected void notifyGamelanNodeCancelled(AgentExecutionBinding binding) {
        // no-op stub — the Gamelan adapter overrides this
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * A binding is stale when it is already completed or has been superseded by a
     * newer Gamelan attempt for the same node (6.17.11 duplicate/stale protection).
     */
    public boolean isCurrentDispatch(AgentExecutionBinding binding) {
        if (!binding.isActive()) {
            return false;
        }
        return store.findActiveForNode(binding.tenantId(), binding.workflowRunId(), binding.nodeId())
                .map(active -> active.executionId().equals(binding.executionId()))
                .orElse(false);
    }

    int bindingCount() {
        return store instanceof InMemoryGamelanExecutionBindingStore inMemory ? inMemory.size() : -1;
    }
}
