package tech.kayys.andalus.a2a.durable;

import tech.kayys.andalus.a2a.api.A2AClient;
import tech.kayys.andalus.a2a.model.A2ATask;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;

import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

/**
 * Default {@link A2AReconciler} backed by {@link A2AClient#getTask(String)}.
 *
 * <p>Mapping (6.16.6.8):</p>
 * <ul>
 *   <li>remote task found, non-terminal → {@link A2ARecoveryDecision#FOUND_RUNNING}</li>
 *   <li>remote COMPLETED / FAILED / CANCELLED → the matching {@code FOUND_*} decision</li>
 *   <li>remote reports "no such task" → {@link A2ARecoveryDecision#NOT_FOUND}
 *       (distinct from failure — task lookup may be eventually consistent)</li>
 *   <li>transport/network error → {@link A2ARecoveryDecision#UNKNOWN}
 *       (never treated as failure, never blindly retried as a new submission)</li>
 * </ul>
 */
public final class A2AClientTaskReconciler implements A2AReconciler {

    private static final Logger LOG = Logger.getLogger(A2AClientTaskReconciler.class.getName());

    private final A2AClient client;

    public A2AClientTaskReconciler(A2AClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    @Override
    public CompletionStage<A2ARecoveryDecision> reconcile(A2AInvocationIntent intent, DurableA2ATask task) {
        return client.getTask(task.taskId())
                .thenApply(A2AClientTaskReconciler::toDecision)
                .exceptionally(error -> {
                    Throwable cause = unwrap(error);
                    if (cause instanceof IllegalArgumentException) {
                        // A2A servers signal unknown tasks as IllegalArgumentException
                        // (see DurableA2AAgentAdapter#getTask).
                        return A2ARecoveryDecision.NOT_FOUND;
                    }
                    LOG.warning("A2A reconciliation of task " + task.taskId()
                            + " inconclusive: " + cause.getMessage());
                    return A2ARecoveryDecision.UNKNOWN;
                });
    }

    static A2ARecoveryDecision toDecision(A2ATask remote) {
        A2ATaskStatus status = remote.status();
        if (status == null) {
            return A2ARecoveryDecision.UNKNOWN;
        }
        return switch (status) {
            case COMPLETED -> A2ARecoveryDecision.FOUND_COMPLETED;
            case FAILED -> A2ARecoveryDecision.FOUND_FAILED;
            case CANCELLED -> A2ARecoveryDecision.FOUND_CANCELLED;
            default -> A2ARecoveryDecision.FOUND_RUNNING;
        };
    }

    private static Throwable unwrap(Throwable error) {
        Throwable t = error;
        while ((t instanceof java.util.concurrent.CompletionException
                || t instanceof java.util.concurrent.ExecutionException) && t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }
}
