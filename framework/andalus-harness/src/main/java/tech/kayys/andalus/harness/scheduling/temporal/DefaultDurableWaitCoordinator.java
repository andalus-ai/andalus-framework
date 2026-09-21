package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.DefaultExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionStateStore;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;
import tech.kayys.andalus.harness.journal.DefaultExecutionEvent;
import tech.kayys.andalus.harness.journal.EventId;
import tech.kayys.andalus.harness.journal.EventSequence;
import tech.kayys.andalus.harness.journal.ExecutionEventType;
import tech.kayys.andalus.harness.journal.ExecutionJournal;
import tech.kayys.andalus.harness.journal.JournalPosition;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Standard implementation of {@link DurableWaitCoordinator} (6.15).
 *
 * <p>This implementation keeps the zero-overhead architecture described in 6.15.24:
 * it re-uses {@link ExecutionStateStore}, {@link ExecutionJournal}, and {@link Deadline}
 * rather than introducing a separate WaitManager or PersistentAgentThread.
 *
 * <p>The {@link WakeCondition} is stored in the execution journal payload at
 * {@code EXECUTION_WAITING} time so it survives process crashes and can be restored on restart.
 *
 * <p><strong>Thread safety:</strong> The coordinator is stateless; state lives entirely in
 * the injected {@code stateStore} and {@code journal}.
 */
public class DefaultDurableWaitCoordinator implements DurableWaitCoordinator {

    private final ExecutionStateStore stateStore;
    private final ExecutionJournal journal;

    public DefaultDurableWaitCoordinator(ExecutionStateStore stateStore, ExecutionJournal journal) {
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore");
        this.journal = Objects.requireNonNull(journal, "journal");
    }

    // ── enterWaiting ────────────────────────────────────────────────────────

    @Override
    public void enterWaiting(ExecutionId executionId, WakeCondition wakeCondition, Optional<Deadline> deadline) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(wakeCondition, "wakeCondition");
        Objects.requireNonNull(deadline, "deadline");

        ExecutionState current = stateStore.load(executionId)
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot enter WAITING: execution not found: " + executionId.value()));

        // Terminal executions cannot park
        if (current.status().isTerminal()) {
            throw new IllegalStateException(
                    "Cannot enter WAITING from terminal status: " + current.status());
        }

        // Idempotent: if already WAITING, do nothing
        if (current.status() == ExecutionStatus.WAITING) {
            return;
        }

        // Validate deadline is not already exceeded
        Instant now = Instant.now();
        if (deadline.isPresent() && deadline.get().isExceeded(now)) {
            // Deadline already passed — transition to TIMEOUT instead of WAITING
            ExecutionState timedOut = ((DefaultExecutionState) current).withStatus(ExecutionStatus.TIMEOUT);
            stateStore.save(timedOut, current.version());
            appendEvent(executionId, ExecutionEventType.EXECUTION_EXPIRED, Map.of("reason", "deadline exceeded before entering WAITING"));
            return;
        }

        // Persist WAITING state
        ExecutionState waitingState = ((DefaultExecutionState) current).withStatus(ExecutionStatus.WAITING);
        stateStore.save(waitingState, current.version());

        // Journal the WAITING fact with wake condition metadata (survives crash)
        Map<String, Object> payload = buildWaitingPayload(wakeCondition, deadline);
        appendEvent(executionId, ExecutionEventType.EXECUTION_WAITING, payload);
    }

    // ── wakeExecution ───────────────────────────────────────────────────────

    @Override
    public WakeResult wakeExecution(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId");

        Optional<ExecutionState> opt = stateStore.load(executionId);
        if (opt.isEmpty()) {
            return WakeResult.NOT_FOUND;
        }
        ExecutionState current = opt.get();

        // If already terminal or not WAITING — idempotent no-op
        if (current.status().isTerminal()) {
            return WakeResult.IGNORED;
        }
        if (current.status() != ExecutionStatus.WAITING) {
            return WakeResult.IGNORED;
        }

        // Check deadline from journal (replay EXECUTION_WAITING payload)
        Optional<Instant> deadlineAt = resolveDeadlineFromJournal(executionId);
        Instant now = Instant.now();
        if (deadlineAt.isPresent() && !now.isBefore(deadlineAt.get())) {
            // Deadline exceeded — expire the execution
            ExecutionState expired = ((DefaultExecutionState) current).withStatus(ExecutionStatus.TIMEOUT);
            stateStore.save(expired, current.version());
            appendEvent(executionId, ExecutionEventType.EXECUTION_EXPIRED, Map.of("reason", "deadline exceeded at wake time"));
            return WakeResult.EXPIRED;
        }

        // Transition WAITING → RUNNING (via RESUMING intent, which runtime picks up)
        ExecutionState running = ((DefaultExecutionState) current).withStatus(ExecutionStatus.RUNNING);
        stateStore.save(running, current.version());
        appendEvent(executionId, ExecutionEventType.EXECUTION_RESUMING, Map.of("wakeSource", "DurableWaitCoordinator"));

        return WakeResult.WOKEN;
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Map<String, Object> buildWaitingPayload(WakeCondition condition, Optional<Deadline> deadline) {
        var builder = new java.util.HashMap<String, Object>();
        builder.put("wakeConditionType", condition.getClass().getSimpleName());

        switch (condition) {
            case WakeCondition.TimeWake tw ->
                    builder.put("wakeAt", tw.wakeAt().toString());
            case WakeCondition.EventWake ew ->
                    builder.put("eventFilter", ew.filter().toString());
            case WakeCondition.HumanWake hw ->
                    builder.put("approvalId", hw.approvalId());
            case WakeCondition.ExecutionWake xw ->
                    builder.put("targetExecutionId", xw.targetExecutionId().value());
            case WakeCondition.ConditionWake cw ->
                    builder.put("conditionExpression", cw.conditionExpression());
        }

        deadline.ifPresent(d -> builder.put("deadlineAt", d.at().toString()));
        return Map.copyOf(builder);
    }

    /**
     * Looks up the deadline captured in the {@code EXECUTION_WAITING} journal event.
     * Returns empty if no deadline was recorded.
     */
    private Optional<Instant> resolveDeadlineFromJournal(ExecutionId executionId) {
        // Bridge harness ExecutionId → consistency ExecutionId for journal lookup
        tech.kayys.andalus.harness.consistency.state.ExecutionId journalId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());

        return journal.history(journalId).stream()
                .filter(e -> e.type() == ExecutionEventType.EXECUTION_WAITING)
                .findFirst()
                .flatMap(e -> {
                    Object raw = e.payload().get("deadlineAt");
                    if (raw instanceof String s) {
                        try {
                            return Optional.of(Instant.parse(s));
                        } catch (Exception ignored) {
                            return Optional.empty();
                        }
                    }
                    return Optional.empty();
                });
    }

    private void appendEvent(ExecutionId executionId, ExecutionEventType type, Map<String, Object> payload) {
        tech.kayys.andalus.harness.consistency.state.ExecutionId journalId =
                tech.kayys.andalus.harness.consistency.state.ExecutionId.of(executionId.value());

        JournalPosition pos = journal.position(journalId);
        EventSequence seq = EventSequence.of(pos.sequence() + 1);
        var event = new DefaultExecutionEvent(
                EventId.generate(),
                journalId,
                seq,
                Instant.now(),
                type,
                Optional.empty(),
                payload
        );
        journal.append(event);
    }
}
