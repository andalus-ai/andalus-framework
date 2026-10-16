package tech.kayys.andalus.a2a.durable;

import java.util.concurrent.CompletionStage;

/**
 * A2A integration SPI answering exactly one question (6.16.6.7):
 *
 * <p>"What is the known remote state of this particular A2A operation?"</p>
 *
 * <p>This is not a general recovery manager. It does not decide whether to retry,
 * compensate, or resubmit — those belong to the Claw retry policy, effect analyzer,
 * and compensation subsystem (6.16.6.28).</p>
 *
 * <p>Implementations must be idempotent: reconciling the same remote state any
 * number of times must produce the same local durable outcome (6.16.6.9).</p>
 */
public interface A2AReconciler {

    /**
     * Reconciles the local durable view of a task with the remote agent.
     *
     * @param intent durable identity of the logical A2A invocation (6.16.2.8)
     * @param task   the locally persisted A2A task
     * @return the known remote state of the operation
     */
    CompletionStage<A2ARecoveryDecision> reconcile(A2AInvocationIntent intent, DurableA2ATask task);
}
