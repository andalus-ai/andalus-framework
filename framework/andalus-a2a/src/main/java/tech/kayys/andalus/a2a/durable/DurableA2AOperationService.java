package tech.kayys.andalus.a2a.durable;

import tech.kayys.andalus.a2a.model.A2AArtifact;

import java.util.List;
import java.util.Optional;

/**
 * Durable A2A operation service SPI (6.16).
 *
 * <p>Encapsulates the full durable lifecycle for a single A2A operation:
 * <pre>
 *   create/persist intent   → {@link #beginOperation}
 *   submit to remote        → {@link #submitToRemote}
 *   transition to WAITING   → {@link #enterWaiting}
 *   remote completes        → {@link #completeOperation}
 *   remote fails/cancels    → {@link #failOperation} / {@link #cancelOperation}
 *   reconcile after crash   → {@link #reconcile}
 * </pre>
 *
 * <p><strong>No A2AExecutionEngine, no A2AWaitManager, no A2ACheckpointStore</strong> (6.16.29).
 * Caller execution IDs are passed as plain {@code String} values so this SPI has no
 * compile-time dependency on the harness execution layer (SOLID / separation of concerns).
 * The Claw integration adapter in {@code andalus-harness} wraps this SPI and bridges
 * the typed {@code ExecutionId} ↔ {@code String} boundary.
 */
public interface DurableA2AOperationService {

    /**
     * Creates and durably persists a new A2A operation intent before the remote request is sent.
     * Implements the first part of the 6.16.9 sequence: create OperationId, persist intent, checkpoint.
     *
     * @param callerExecutionId string value of the local execution initiating the A2A call
     * @param remoteEndpoint    URI of the remote agent
     * @param idempotencyKey    stable retry key (null = auto-generate from callerExecutionId + opId)
     * @param checkpointId      checkpoint saved before this operation (resume anchor)
     * @return the persisted operation record (PENDING status)
     */
    A2AOperationRecord beginOperation(String callerExecutionId, String remoteEndpoint,
                                      String idempotencyKey, String checkpointId);

    /**
     * Records that the remote request has been submitted (PENDING → SUBMITTED).
     * If the remote agent has already assigned a task ID, it is stored here.
     *
     * @param operationId the operation created by {@link #beginOperation}
     * @param a2aTaskId   the task ID assigned by the remote agent (may be null if not yet known)
     * @return the updated operation record
     */
    A2AOperationRecord submitToRemote(String operationId, String a2aTaskId);

    /**
     * Transitions the caller execution to WAITING and marks the operation WAITING_FOR_REMOTE.
     * Implements 6.16.5: caller WAITING, worker released. Must be called after
     * {@link #submitToRemote} so a remote task ID is available for the wake condition.
     *
     * @param operationId the operation to wait on
     * @return the updated operation record
     */
    A2AOperationRecord enterWaiting(String operationId);

    /**
     * Records remote task completion, persists the result durably, then wakes the caller.
     * <strong>Result is persisted BEFORE wake</strong> (6.16.21 ordering guarantee — result is
     * never lost even if the process crashes immediately after completion is written).
     *
     * @param operationId the completed operation
     * @param artifacts   remote task output artifacts
     * @return the updated operation record
     */
    A2AOperationRecord completeOperation(String operationId, List<A2AArtifact> artifacts);

    /**
     * Records remote task failure and wakes the caller so it can apply recovery policy (6.16.16).
     *
     * @param operationId the failed operation
     * @param reason      error description from the remote agent
     * @return the updated operation record
     */
    A2AOperationRecord failOperation(String operationId, String reason);

    /**
     * Cancels a remote A2A task and records the local operation as CANCELLED (6.16.16).
     *
     * @param operationId the operation to cancel
     * @return the updated operation record
     */
    A2AOperationRecord cancelOperation(String operationId);

    /**
     * Reconciles an operation in UNKNOWN/WAITING_FOR_REMOTE state after a crash (6.16.14/6.16.26).
     * Checks the remote task status via the ledger and transitions accordingly:
     * <ul>
     *   <li>Remote COMPLETED    → {@link #completeOperation}</li>
     *   <li>Remote RUNNING/PENDING → re-register WAITING (no duplicate request)</li>
     *   <li>Remote FAILED/CANCELLED → {@link #failOperation}/{@link #cancelOperation}</li>
     *   <li>Remote not found    → mark UNKNOWN for operator review</li>
     *   <li>No task ID assigned → reset to PENDING for retry</li>
     * </ul>
     *
     * @param operationId the operation to reconcile
     * @return the updated operation record after reconciliation
     */
    A2AOperationRecord reconcile(String operationId);

    /** Looks up an operation by its operation ID. */
    Optional<A2AOperationRecord> find(String operationId);

    /**
     * Finds all operations for a given caller execution ID string.
     * Used during crash recovery (6.16.26) to reload all pending A2A operations.
     */
    List<A2AOperationRecord> findByCallerExecution(String callerExecutionId);

    /**
     * Finds an existing operation with the given idempotency key.
     * Used to prevent duplicate submission across retries (6.16.13).
     */
    Optional<A2AOperationRecord> findByIdempotencyKey(String idempotencyKey);
}
