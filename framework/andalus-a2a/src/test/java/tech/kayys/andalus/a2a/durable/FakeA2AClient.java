package tech.kayys.andalus.a2a.durable;

import tech.kayys.andalus.a2a.api.A2AClient;
import tech.kayys.andalus.a2a.model.A2AMessage;
import tech.kayys.andalus.a2a.model.A2ATask;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controllable fake remote A2A agent for durable integration tests (6.16.8.34).
 *
 * <p>Maintains its own <em>remote</em> state, deliberately independent of the local
 * ledger, so tests can create disagreement ({@code local=WAITING, remote=COMPLETED}).
 * Supported failure scripts:</p>
 * <ul>
 *   <li>{@link #networkErrors} — {@code getTask}/{@code cancelTask} throw a transport
 *       error (→ {@code UNKNOWN} reconciliation outcome)</li>
 *   <li>{@link #droppedResponses} — {@code getTask} reports "not found"
 *       (→ {@code NOT_FOUND})</li>
 *   <li>{@link #cancelFailures} — remote rejects cancellation</li>
 * </ul>
 *
 * <p>The fake survives simulated restarts (it is the remote system, not the local
 * process), which lets restart tests reuse it across fresh local components.</p>
 */
public final class FakeA2AClient implements A2AClient {

    /** taskId → remote truth */
    public final Map<String, A2ATaskStatus> remoteState = new ConcurrentHashMap<>();

    /** taskIds for which getTask reports transport failure (UNKNOWN). */
    public final Set<String> networkErrors = ConcurrentHashMap.newKeySet();

    /** taskIds for which getTask reports "not found" (NOT_FOUND). */
    public final Set<String> droppedResponses = ConcurrentHashMap.newKeySet();

    /** taskIds for which cancelTask fails. */
    public final Set<String> cancelFailures = ConcurrentHashMap.newKeySet();

    /** Recorded interactions (for "must not resubmit" assertions). */
    public final List<A2AMessage> sentMessages = new CopyOnWriteArrayList<>();
    public final List<String> cancelledTasks = new CopyOnWriteArrayList<>();
    public final List<String> polledTasks = new CopyOnWriteArrayList<>();

    /** Remote accepts the message and starts RUNNING. */
    @Override
    public CompletionStage<A2ATask> sendMessage(A2AMessage message) {
        sentMessages.add(message);
        String taskId = "remote-task-" + sentMessages.size();
        remoteState.put(taskId, A2ATaskStatus.RUNNING);
        return CompletableFuture.completedFuture(
                new A2ATask(taskId, "exec-remote-" + taskId, A2ATaskStatus.RUNNING, List.of(), Map.of()));
    }

    @Override
    public CompletionStage<A2ATask> getTask(String taskId) {
        polledTasks.add(taskId);
        if (networkErrors.contains(taskId)) {
            return CompletableFuture.failedFuture(new RuntimeException("connection refused"));
        }
        if (droppedResponses.contains(taskId)) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Task not found: " + taskId));
        }
        A2ATaskStatus status = remoteState.get(taskId);
        if (status == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Task not found: " + taskId));
        }
        return CompletableFuture.completedFuture(
                new A2ATask(taskId, "exec-remote-" + taskId, status, List.of(), Map.of()));
    }

    /** Idempotent remote cancellation: already-cancelled/completed is acknowledged. */
    @Override
    public CompletionStage<Void> cancelTask(String taskId) {
        if (cancelFailures.contains(taskId)) {
            return CompletableFuture.failedFuture(new RuntimeException("cancel rejected"));
        }
        cancelledTasks.add(taskId);
        remoteState.computeIfPresent(taskId, (id, s) ->
                s == A2ATaskStatus.COMPLETED || s == A2ATaskStatus.FAILED ? s : A2ATaskStatus.CANCELLED);
        return CompletableFuture.completedFuture(null);
    }
}
