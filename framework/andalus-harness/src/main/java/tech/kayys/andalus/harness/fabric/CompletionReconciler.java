package tech.kayys.andalus.harness.fabric;

import tech.kayys.andalus.execution.attempt.AttemptState;
import tech.kayys.andalus.execution.attempt.ExecutionAttempt;
import tech.kayys.andalus.execution.attempt.ExecutionAttemptStore;
import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.ExecutionStatus;
import tech.kayys.andalus.execution.mobility.LeaseManager;
import tech.kayys.andalus.execution.mobility.state.ExecutionStateStore;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Repairs partially committed completion intents after worker loss.
 *
 * <p>Every reconciliation acquires a new execution lease. The original execution
 * fence and queue claim in the intent are audit data only and are never reused.
 * Queue acknowledgement is deliberately outside this coordinator.</p>
 */
public final class CompletionReconciler {
    public enum Outcome { COMPLETED, DEFERRED_ACTIVE_LEASE }

    private final CompletionIntentStore intents;
    private final ExecutionStateStore executionStates;
    private final ExecutionAttemptStore attempts;
    private final LeaseManager leases;
    private final String holderId;
    private final Duration leaseDuration;

    public CompletionReconciler(
            CompletionIntentStore intents,
            ExecutionStateStore executionStates,
            ExecutionAttemptStore attempts,
            LeaseManager leases,
            String holderId,
            Duration leaseDuration) {
        this.intents = Objects.requireNonNull(intents, "intents cannot be null");
        this.executionStates = Objects.requireNonNull(executionStates, "executionStates cannot be null");
        this.attempts = Objects.requireNonNull(attempts, "attempts cannot be null");
        this.leases = Objects.requireNonNull(leases, "leases cannot be null");
        if (holderId == null || holderId.isBlank())
            throw new IllegalArgumentException("holderId cannot be blank");
        this.holderId = holderId;
        this.leaseDuration = Objects.requireNonNull(leaseDuration, "leaseDuration cannot be null");
        if (leaseDuration.isZero() || leaseDuration.isNegative())
            throw new IllegalArgumentException("leaseDuration must be positive");
    }

    /** Reconcile a bounded oldest-first batch; no queue ack is issued here. */
    public List<Outcome> reconcileIncomplete(int limit) {
        List<CompletionIntent> pending = intents.listIncomplete(limit);
        List<Outcome> outcomes = new ArrayList<>(pending.size());
        for (CompletionIntent intent : pending) {
            outcomes.add(reconcile(intent.idempotencyKey()));
        }
        return List.copyOf(outcomes);
    }

    public Outcome reconcile(String idempotencyKey) {
        CompletionIntent intent = intents.get(idempotencyKey)
                .orElseThrow(() -> new IllegalArgumentException("Unknown completion intent: " + idempotencyKey));
        if (intent.stage() == CompletionIntentStage.COMPLETED) return Outcome.COMPLETED;

        final ExecutionLease lease;
        try {
            lease = leases.acquireLease(intent.executionId(), holderId, leaseDuration);
        } catch (IllegalStateException activeOwner) {
            return Outcome.DEFERRED_ACTIVE_LEASE;
        }

        try {
            if (!leases.validateToken(intent.executionId(), lease.fencingToken()))
                throw new IllegalStateException("Fresh reconciliation lease was fenced before use");

            intent = intents.get(idempotencyKey).orElseThrow();
            if (intent.stage() == CompletionIntentStage.RECORDED) {
                commitExecutionState(intent, lease);
                advance(intent, CompletionIntentStage.EXECUTION_STATE_COMMITTED);
            }

            intent = intents.get(idempotencyKey).orElseThrow();
            if (intent.stage() == CompletionIntentStage.EXECUTION_STATE_COMMITTED) {
                commitAttempt(intent, lease);
                advance(intent, CompletionIntentStage.ATTEMPT_COMMITTED);
            }

            intent = intents.get(idempotencyKey).orElseThrow();
            if (intent.stage() == CompletionIntentStage.ATTEMPT_COMMITTED) {
                advance(intent, CompletionIntentStage.COMPLETED);
            }

            CompletionIntent completed = intents.get(idempotencyKey).orElseThrow();
            if (completed.stage() != CompletionIntentStage.COMPLETED)
                throw new IllegalStateException("Completion intent did not reach COMPLETED");
            return Outcome.COMPLETED;
        } finally {
            leases.releaseLease(intent.executionId(), holderId);
        }
    }

    private void commitExecutionState(CompletionIntent intent, ExecutionLease lease) {
        ExecutionDurableState current = executionStates.load(intent.executionId())
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot reconcile completion without durable execution state: " + intent.executionId()));
        if (current.status() == ExecutionStatus.COMPLETED) return;
        if (current.status() != ExecutionStatus.RUNNING)
            throw new IllegalStateException("Refusing to complete execution from unexpected state " + current.status());
        executionStates.save(current.withStatus(ExecutionStatus.COMPLETED), lease.fencingToken());
    }

    private void commitAttempt(CompletionIntent intent, ExecutionLease lease) {
        ExecutionAttempt current = attempts.get(intent.physicalAttemptId())
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot reconcile missing physical attempt: " + intent.physicalAttemptId().value()));
        if (!current.executionId().equals(intent.executionId()))
            throw new IllegalStateException("Physical attempt belongs to a different execution");
        if (current.state() == AttemptState.COMPLETED) return;
        if (current.state() != AttemptState.RUNNING)
            throw new IllegalStateException("Refusing to complete attempt from unexpected state " + current.state());
        attempts.updateState(intent.physicalAttemptId(), AttemptState.COMPLETED, lease.fencingToken());
    }

    private void advance(CompletionIntent intent, CompletionIntentStage next) {
        if (!intents.advance(intent.idempotencyKey(), intent.stage(), next)) {
            CompletionIntent latest = intents.get(intent.idempotencyKey()).orElseThrow();
            if (latest.stage().ordinal() < next.ordinal())
                throw new IllegalStateException("Completion intent stage changed concurrently");
        }
    }
}
