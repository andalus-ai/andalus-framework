package tech.kayys.andalus.harness.coordination;

import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionState;

import java.util.Optional;

/**
 * Reconciliation bridge between Gamelan workflow semantics and Andalus Claw execution semantics (6.17).
 *
 * <p><strong>Design principle (6.17.1/6.17.2):</strong> This is a <em>reconciliation adapter</em>,
 * not a second orchestrator. It answers:
 * <blockquote>
 *   "Given the durable state of this Gamelan node and the durable state of the corresponding
 *   Andalus execution, what synchronization action is required?"
 * </blockquote>
 *
 * <p>Ownership boundaries:
 * <pre>
 *   Gamelan owns:   workflow graph, DAG, node ordering, workflow retry, workflow variables
 *   Andalus owns:   ExecutionId, checkpoint, journal, WAITING, crash recovery, A2A lifecycle
 * </pre>
 *
 * <p>The bridge translates outcomes between the two systems without becoming the owner
 * of either state machine (6.17.12).
 *
 * <p><strong>No duplication rule (6.17.12):</strong> Never bypass
 * {@code WorkflowRunManager}. All Gamelan state changes must go through Gamelan's
 * own commit services; the bridge only calls Claw APIs on the Andalus side.
 */
public interface GamelanClawBridge {

    /**
     * Called by Gamelan after it atomically reserves a node for dispatch.
     *
     * <p>The bridge creates (or recovers) a Claw execution for the given node and persists
     * the {@link AgentExecutionBinding} so both sides share a durable correlation (6.17.5/6.17.6).
     *
     * @param tenantId      tenant scope (cross-tenant binding is never allowed — 6.16.12)
     * @param workflowRunId Gamelan run ID
     * @param nodeId        Gamelan node ID within the run
     * @param reservationId the atomic dispatch reservation ID from {@code WorkflowRunManager}
     * @return a newly created (or idempotently recovered) {@link AgentExecutionBinding}
     */
    AgentExecutionBinding bindNodeExecution(String tenantId, String workflowRunId,
                                             String nodeId, String reservationId);

    /**
     * Attempt-aware variant (6.17.2.21): each Gamelan node retry is a distinct dispatch
     * identity ({@code tenant + run + node + attempt}), so a retry produces a
     * <em>new</em> binding and supersedes — but never mutates — the previous attempt's
     * binding. Idempotent for a repeated reservation of the same attempt.
     *
     * @param attempt 1-based Gamelan node attempt number
     * @throws BindingConflictException if this dispatch is already bound to another execution
     */
    AgentExecutionBinding bindNodeExecution(String tenantId, String workflowRunId,
                                             String nodeId, int attempt, String reservationId);

    /**
     * Called when the Claw execution for a bound node completes successfully.
     *
     * <p>The bridge notifies Gamelan so it can invoke {@code handleNodeResult} and
     * compute the next ready nodes. Result data must already be durably persisted in
     * the Claw journal before this method is called (result-then-wake ordering).
     *
     * @param binding      the binding identifying which Gamelan node completed
     * @param finalState   the terminal Claw execution state carrying the result
     */
    void onExecutionCompleted(AgentExecutionBinding binding, ExecutionState finalState);

    /**
     * Called when the Claw execution for a bound node fails.
     *
     * <p>The bridge reports the failure to Gamelan, which decides whether to retry the node,
     * start compensation, or fail the workflow. Andalus retry semantics (AttemptId) are
     * already exhausted at this point.
     *
     * @param binding   the binding identifying which Gamelan node failed
     * @param reason    human-readable failure description
     */
    void onExecutionFailed(AgentExecutionBinding binding, String reason);

    /**
     * Called when the Claw execution for a bound node is cancelled.
     *
     * @param binding   the binding identifying which Gamelan node was cancelled
     */
    void onExecutionCancelled(AgentExecutionBinding binding);

    /**
     * Reconciles a Gamelan node whose Claw execution is in an unexpected state
     * (e.g. after a crash where both Gamelan and Claw need to re-sync — 6.17.7/6.17.8).
     *
     * <p>Reconciliation scenarios:
     * <ul>
     *   <li>Binding found, Claw COMPLETED → call {@link #onExecutionCompleted}</li>
     *   <li>Binding found, Claw RUNNING/WAITING → Gamelan node stays IN_PROGRESS</li>
     *   <li>Binding found, Claw FAILED → call {@link #onExecutionFailed}</li>
     *   <li>No binding found → Gamelan may re-dispatch or mark the node as lost</li>
     * </ul>
     *
     * @param tenantId      tenant scope
     * @param workflowRunId workflow run to reconcile
     * @param nodeId        specific node to reconcile
     * @return the current binding if one exists
     */
    Optional<AgentExecutionBinding> reconcileNode(String tenantId, String workflowRunId, String nodeId);

    /**
     * Looks up the active binding for a given Claw execution (used during wake/resume — 6.17.11).
     *
     * @param executionId the Claw execution ID
     * @return the binding if this execution is associated with a Gamelan node
     */
    Optional<AgentExecutionBinding> findByExecutionId(ExecutionId executionId);
}
