package tech.kayys.andalus.harness.coordination;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * In-memory {@link GamelanExecutionBindingStore} for standalone runs and tests
 * (6.17.2.31).
 *
 * <p>Concurrency: bindings are keyed by the derived dispatch identity and inserted
 * with {@code compute}, so two concurrent bind attempts for the same dispatch cannot
 * both win — the loser observes a {@link BindingConflictException} (6.17.2.17).</p>
 */
public class InMemoryGamelanExecutionBindingStore implements GamelanExecutionBindingStore {

    private static final Logger LOG = Logger.getLogger(InMemoryGamelanExecutionBindingStore.class.getName());

    /** dispatchId → binding (includes completed bindings — 6.17.2.23). */
    private final Map<String, AgentExecutionBinding> byDispatch = new ConcurrentHashMap<>();

    /** executionId.value() → dispatchId */
    private final Map<String, String> dispatchByExecution = new ConcurrentHashMap<>();

    @Override
    public Optional<AgentExecutionBinding> find(String tenantId, String workflowRunId,
                                                String nodeId, int attempt) {
        if (tenantId == null || workflowRunId == null || nodeId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byDispatch.get(dispatchKey(tenantId, workflowRunId, nodeId, attempt)));
    }

    @Override
    public Optional<AgentExecutionBinding> findByExecutionId(ExecutionId executionId) {
        if (executionId == null) {
            return Optional.empty();
        }
        String dispatchId = dispatchByExecution.get(executionId.value());
        return dispatchId == null ? Optional.empty() : Optional.ofNullable(byDispatch.get(dispatchId));
    }

    @Override
    public Optional<AgentExecutionBinding> findActiveForNode(String tenantId, String workflowRunId,
                                                             String nodeId) {
        if (tenantId == null || workflowRunId == null || nodeId == null) {
            return Optional.empty();
        }
        return byDispatch.values().stream()
                .filter(AgentExecutionBinding::isActive)
                .filter(b -> b.tenantId().equals(tenantId))
                .filter(b -> b.workflowRunId().equals(workflowRunId))
                .filter(b -> b.nodeId().equals(nodeId))
                // Deterministic: the newest Gamelan attempt owns the node (6.17.2.21).
                .max(java.util.Comparator.comparingInt(AgentExecutionBinding::attempt));
    }

    @Override
    public AgentExecutionBinding bind(AgentExecutionBinding binding) {
        Objects.requireNonNull(binding, "binding");

        AgentExecutionBinding stored = byDispatch.compute(binding.dispatchId(), (dispatchId, existing) -> {
            if (existing == null) {
                return binding;
            }
            if (existing.executionId().equals(binding.executionId())) {
                return existing; // idempotent replay (6.17.2.16)
            }
            throw new BindingConflictException(dispatchId,
                    existing.executionId().value(), binding.executionId().value());
        });

        // Execution uniqueness: one active dispatch per execution (6.17.2.16)
        String claimed = dispatchByExecution.putIfAbsent(
                stored.executionId().value(), stored.dispatchId());
        if (claimed != null && !claimed.equals(stored.dispatchId())) {
            throw new BindingConflictException(stored.executionId().value() + "-execution",
                    claimed, stored.dispatchId());
        }

        LOG.fine(() -> "Gamelan binding persisted: " + stored.dispatchId()
                + " -> exec=" + stored.executionId().value());
        return stored;
    }

    @Override
    public void complete(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId");
        String dispatchId = dispatchByExecution.get(executionId.value());
        if (dispatchId == null) {
            return;
        }
        byDispatch.computeIfPresent(dispatchId, (id, existing) -> existing.withCompleted());
    }

    @Override
    public void remove(AgentExecutionBinding binding) {
        Objects.requireNonNull(binding, "binding");
        byDispatch.remove(binding.dispatchId(), binding);
        dispatchByExecution.remove(binding.executionId().value(), binding.dispatchId());
    }

    // ── Test/observability helpers ────────────────────────────────────────────

    public int size() {
        return byDispatch.size();
    }

    static String dispatchKey(String tenantId, String workflowRunId, String nodeId, int attempt) {
        return tenantId + ":" + workflowRunId + ":" + nodeId + ":" + attempt;
    }
}
