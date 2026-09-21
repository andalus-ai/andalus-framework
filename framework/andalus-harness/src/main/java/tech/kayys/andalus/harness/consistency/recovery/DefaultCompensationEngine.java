package tech.kayys.andalus.harness.consistency.recovery;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.harness.journal.DefaultExecutionEvent;
import tech.kayys.andalus.harness.journal.EventSequence;
import tech.kayys.andalus.harness.journal.ExecutionEventType;
import tech.kayys.andalus.harness.journal.ExecutionJournal;
import tech.kayys.andalus.harness.journal.JournalPosition;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Durable saga-style compensation engine (6.14).
 *
 * <p>Executes compensatable steps in <strong>reverse</strong> successful order,
 * journaling each lifecycle fact so the process can crash mid-compensation and
 * safely resume from where it left off on restart.
 *
 * <p><strong>Idempotency:</strong> If a step's compensation was already journaled as
 * {@link ExecutionEventType#COMPENSATION_ACTION_COMPLETED} or
 * {@link ExecutionEventType#COMPENSATION_ACTION_FAILED}, the engine skips re-executing it.
 * This allows restart recovery to replay compensation without double-executing external calls.
 *
 * <p><strong>Partial failure visibility:</strong> If a step's compensation fails, the failure
 * is journaled as {@link ExecutionEventType#COMPENSATION_ACTION_FAILED} and execution continues
 * to the remaining steps. The final {@link CompensationSummary} surfaces every failed step ID
 * so operators can see unresolved external effects.
 */
public class DefaultCompensationEngine implements CompensationEngine {

    private final ExecutionJournal journal;

    public DefaultCompensationEngine(ExecutionJournal journal) {
        this.journal = Objects.requireNonNull(journal, "journal");
    }

    @Override
    public CompensationSummary compensate(
            ExecutionId executionId,
            List<ExecutionStep> steps,
            Map<String, Object> context) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(steps, "steps");

        CompensationSummary.Builder summary = CompensationSummary.builder(executionId);

        if (steps.isEmpty()) {
            return summary.build();
        }

        // Journal COMPENSATION_STARTED (idempotent: only append if not already present)
        if (!alreadyStarted(executionId)) {
            appendEvent(executionId, ExecutionEventType.COMPENSATION_STARTED, Map.of());
        }

        // Compute which steps are already resolved from journal (crash-safe resume)
        java.util.Set<String> alreadyResolved = resolvedStepIds(executionId);

        // Execute in reverse order (last-in, first-compensated)
        List<ExecutionStep> reversed = steps.stream()
                .filter(ExecutionStep::isCompensatable)
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        Collections.reverse(reversed);

        // Steps that are not compensatable still count as skipped
        int skipCount = (int) steps.stream().filter(s -> !s.isCompensatable()).count();
        for (int i = 0; i < skipCount; i++) summary.skipped();

        for (ExecutionStep step : reversed) {
            String stepId = step.id().value();
            summary.attempted();

            // Idempotency: skip if already compensated or failed in a prior run
            if (alreadyResolved.contains(stepId)) {
                summary.compensated(); // treat already-resolved as successful from this run's perspective
                continue;
            }

            appendEvent(executionId, ExecutionEventType.COMPENSATION_ACTION_STARTED,
                    Map.of("stepId", stepId));

            CompensationResult result = step.compensate(context);

            if (result.success()) {
                appendEvent(executionId, ExecutionEventType.COMPENSATION_ACTION_COMPLETED,
                        Map.of("stepId", stepId));
                summary.compensated();
            } else {
                appendEvent(executionId, ExecutionEventType.COMPENSATION_ACTION_FAILED,
                        Map.of("stepId", stepId, "error", result.message()));
                summary.failed(stepId);
            }
        }

        // Journal terminal compensation event
        CompensationSummary built = summary.build();
        if (built.overallSuccess()) {
            appendEvent(executionId, ExecutionEventType.COMPENSATION_COMPLETED,
                    Map.of("stepsCompensated", built.stepsCompensated()));
        } else {
            appendEvent(executionId, ExecutionEventType.COMPENSATION_FAILED,
                    Map.of("stepsFailed", built.stepsFailed(), "failedStepIds", built.failedStepIds()));
        }

        return built;
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private boolean alreadyStarted(ExecutionId executionId) {
        return journal.history(executionId).stream()
                .anyMatch(e -> e.type() == ExecutionEventType.COMPENSATION_STARTED);
    }

    /**
     * Returns step IDs that have already been resolved (completed or failed)
     * in a previous compensation attempt — used to skip re-executing them on restart.
     */
    private java.util.Set<String> resolvedStepIds(ExecutionId executionId) {
        java.util.Set<String> resolved = new java.util.HashSet<>();
        for (var e : journal.history(executionId)) {
            if (e.type() == ExecutionEventType.COMPENSATION_ACTION_COMPLETED
                    || e.type() == ExecutionEventType.COMPENSATION_ACTION_FAILED) {
                Object stepId = e.payload().get("stepId");
                if (stepId instanceof String s) resolved.add(s);
            }
        }
        return resolved;
    }

    private void appendEvent(ExecutionId executionId, ExecutionEventType type, Map<String, Object> payload) {
        JournalPosition pos = journal.position(executionId);
        EventSequence seq = EventSequence.of(pos.sequence() + 1);
        var event = new DefaultExecutionEvent(
                tech.kayys.andalus.harness.journal.EventId.generate(),
                executionId,
                seq,
                java.time.Instant.now(),
                type,
                java.util.Optional.empty(),
                payload
        );
        journal.append(event);
    }
}
