package tech.kayys.andalus.harness.coordination;

import tech.kayys.andalus.harness.execution.state.ExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionStateStore;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Startup reconciliation between Gamelan dispatches and Claw executions (6.17.3).
 *
 * <p>Given the dispatches Gamelan still considers active, it answers — per dispatch —
 * what synchronization action is required, using only authoritative stores:</p>
 * <pre>
 *   Gamelan-active dispatch + missing Claw binding      → REBIND_REQUIRED
 *   binding + missing Claw execution                    → MARK_LOST
 *   binding + terminal Claw execution                   → NOTIFY_*
 *   binding + live Claw execution                       → KEEP_WAITING
 *   superseded attempt (newer attempt owns the node)    → STALE_DISPATCH
 * </pre>
 *
 * <p>Recovery is deliberately asymmetric (6.17.3.25): Claw never discovers workflows, and
 * never mutates Gamelan state — it reports actions; Gamelan's commit services decide.
 * Nothing here scans all executions or executes work (6.17.3.21/6.17.3.24), and the
 * method is idempotent, so multi-instance startups are safe (6.17.3.26).</p>
 */
public final class GamelanClawStartupReconciler {

    private static final Logger LOG = Logger.getLogger(GamelanClawStartupReconciler.class.getName());

    private final GamelanExecutionBindingStore bindings;
    private final ExecutionStateStore executionStates;

    public GamelanClawStartupReconciler(GamelanExecutionBindingStore bindings,
                                        ExecutionStateStore executionStates) {
        this.bindings = Objects.requireNonNull(bindings, "bindings");
        this.executionStates = Objects.requireNonNull(executionStates, "executionStates");
    }

    /** Reconciles every given dispatch. Idempotent and non-blocking. */
    public List<GamelanClawReconciliation> reconcile(List<GamelanDispatchDescriptor> activeDispatches) {
        Objects.requireNonNull(activeDispatches, "activeDispatches");
        List<GamelanClawReconciliation> results = new ArrayList<>(activeDispatches.size());
        for (GamelanDispatchDescriptor dispatch : activeDispatches) {
            results.add(reconcileOne(dispatch));
        }
        return List.copyOf(results);
    }

    /** Reconciles a single dispatch (6.17.3.12–6.17.3.16). */
    public GamelanClawReconciliation reconcileOne(GamelanDispatchDescriptor dispatch) {
        Objects.requireNonNull(dispatch, "dispatch");

        Optional<AgentExecutionBinding> binding = bindings.find(
                dispatch.tenantId(), dispatch.workflowRunId(), dispatch.nodeId(), dispatch.attempt());

        // Case 3/4: Gamelan is dispatching a node with no Claw binding.
        if (binding.isEmpty()) {
            LOG.info(() -> "Startup reconcile: " + dispatch.dispatchId()
                    + " has no Claw binding — rebind required");
            return new GamelanClawReconciliation(
                    dispatch, Optional.empty(), GamelanReconciliationAction.REBIND_REQUIRED, Optional.empty());
        }

        AgentExecutionBinding found = binding.get();

        // Case 5: a newer Gamelan attempt owns this node; this dispatch is historical.
        Optional<AgentExecutionBinding> active =
                bindings.findActiveForNode(dispatch.tenantId(), dispatch.workflowRunId(), dispatch.nodeId());
        if (active.isPresent() && !active.get().executionId().equals(found.executionId())) {
            LOG.info(() -> "Startup reconcile: " + dispatch.dispatchId()
                    + " superseded by " + active.get().dispatchId());
            return new GamelanClawReconciliation(
                    dispatch, Optional.of(found), GamelanReconciliationAction.STALE_DISPATCH, Optional.empty());
        }

        Optional<ExecutionState> state = executionStates.load(found.executionId());

        // Case 2: binding points at an execution that no longer exists (lost worker).
        if (state.isEmpty()) {
            LOG.warning(() -> "Startup reconcile: binding " + found.dispatchId()
                    + " references missing execution " + found.executionId().value());
            return new GamelanClawReconciliation(
                    dispatch, Optional.of(found), GamelanReconciliationAction.MARK_LOST, Optional.empty());
        }

        ExecutionStatus status = state.get().status();

        // Case 1: binding + execution exist. Terminal executions must be re-notified
        // (the crash may have swallowed the original notification — 6.17.2.24);
        // completed bindings are retained, so this is idempotent.
        GamelanReconciliationAction action = switch (status) {
            case COMPLETED -> GamelanReconciliationAction.NOTIFY_COMPLETED;
            case FAILED -> GamelanReconciliationAction.NOTIFY_FAILED;
            case CANCELED -> GamelanReconciliationAction.NOTIFY_CANCELLED;
            default -> GamelanReconciliationAction.KEEP_WAITING;
        };

        return new GamelanClawReconciliation(dispatch, Optional.of(found), action, Optional.of(status));
    }
}
