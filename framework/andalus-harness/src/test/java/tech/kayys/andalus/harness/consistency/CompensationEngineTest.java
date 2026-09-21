package tech.kayys.andalus.harness.consistency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.consistency.recovery.CompensationEngine;
import tech.kayys.andalus.harness.consistency.recovery.CompensationResult;
import tech.kayys.andalus.harness.consistency.recovery.CompensationSummary;
import tech.kayys.andalus.harness.consistency.recovery.DefaultCompensationEngine;
import tech.kayys.andalus.harness.consistency.recovery.ExecutionStep;
import tech.kayys.andalus.harness.consistency.recovery.StepId;
import tech.kayys.andalus.harness.consistency.recovery.StepResult;
import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.harness.journal.ExecutionEventType;
import tech.kayys.andalus.harness.journal.InMemoryExecutionJournal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the 6.14 Compensation Engine.
 */
class CompensationEngineTest {

    private InMemoryExecutionJournal journal;
    private CompensationEngine engine;
    private ExecutionId executionId;

    @BeforeEach
    void setUp() {
        journal = new InMemoryExecutionJournal();
        engine = new DefaultCompensationEngine(journal);
        executionId = ExecutionId.of("exec-comp-001");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static ExecutionStep successStep(String id) {
        return new ExecutionStep() {
            @Override public StepId id() { return StepId.of(id); }
            @Override public boolean isCompensatable() { return true; }
            @Override public StepResult execute(Map<String, Object> ctx) { return StepResult.ok("done"); }
            @Override public CompensationResult compensate(Map<String, Object> ctx) {
                return CompensationResult.compensated();
            }
        };
    }

    private static ExecutionStep failingCompensationStep(String id) {
        return new ExecutionStep() {
            @Override public StepId id() { return StepId.of(id); }
            @Override public boolean isCompensatable() { return true; }
            @Override public StepResult execute(Map<String, Object> ctx) { return StepResult.ok("done"); }
            @Override public CompensationResult compensate(Map<String, Object> ctx) {
                return CompensationResult.failed("external system unavailable");
            }
        };
    }

    private static ExecutionStep nonCompensatableStep(String id) {
        return new ExecutionStep() {
            @Override public StepId id() { return StepId.of(id); }
            @Override public boolean isCompensatable() { return false; }
            @Override public StepResult execute(Map<String, Object> ctx) { return StepResult.ok("done"); }
            @Override public CompensationResult compensate(Map<String, Object> ctx) {
                throw new UnsupportedOperationException("should not be called");
            }
        };
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    void emptySteps_returnsEmptySummary() {
        CompensationSummary result = engine.compensate(executionId, List.of(), Map.of());
        assertEquals(0, result.stepsAttempted());
        assertEquals(0, result.stepsCompensated());
        assertTrue(result.overallSuccess());
    }

    @Test
    void allSuccessSteps_compensatesInReverseOrder() {
        // Steps: A → B → C  (forward order)
        // Compensation should execute: C.compensate, B.compensate, A.compensate
        java.util.List<String> compensationOrder = new java.util.ArrayList<>();

        ExecutionStep stepA = new ExecutionStep() {
            @Override public StepId id() { return StepId.of("A"); }
            @Override public boolean isCompensatable() { return true; }
            @Override public StepResult execute(Map<String, Object> ctx) { return StepResult.ok("ok"); }
            @Override public CompensationResult compensate(Map<String, Object> ctx) {
                compensationOrder.add("A");
                return CompensationResult.compensated();
            }
        };
        ExecutionStep stepB = new ExecutionStep() {
            @Override public StepId id() { return StepId.of("B"); }
            @Override public boolean isCompensatable() { return true; }
            @Override public StepResult execute(Map<String, Object> ctx) { return StepResult.ok("ok"); }
            @Override public CompensationResult compensate(Map<String, Object> ctx) {
                compensationOrder.add("B");
                return CompensationResult.compensated();
            }
        };
        ExecutionStep stepC = new ExecutionStep() {
            @Override public StepId id() { return StepId.of("C"); }
            @Override public boolean isCompensatable() { return true; }
            @Override public StepResult execute(Map<String, Object> ctx) { return StepResult.ok("ok"); }
            @Override public CompensationResult compensate(Map<String, Object> ctx) {
                compensationOrder.add("C");
                return CompensationResult.compensated();
            }
        };

        CompensationSummary summary = engine.compensate(executionId, List.of(stepA, stepB, stepC), Map.of());

        assertEquals(3, summary.stepsCompensated());
        assertTrue(summary.overallSuccess());
        assertEquals(List.of("C", "B", "A"), compensationOrder, "Compensation must happen in reverse order");
    }

    @Test
    void partialFailure_surfacesFailedStepIds() {
        // Reserve inventory ✓, charge payment ✗ compensation
        CompensationSummary summary = engine.compensate(executionId,
                List.of(successStep("reserve-inventory"), failingCompensationStep("charge-payment")),
                Map.of());

        assertFalse(summary.overallSuccess(), "Partial compensation failure → not overall success");
        assertEquals(1, summary.stepsFailed());
        assertTrue(summary.failedStepIds().contains("charge-payment"));
    }

    @Test
    void nonCompensatableSteps_areSkipped() {
        CompensationSummary summary = engine.compensate(executionId,
                List.of(nonCompensatableStep("compute"), successStep("reserve")),
                Map.of());

        assertEquals(1, summary.stepsSkipped());
        assertEquals(1, summary.stepsCompensated());
        assertTrue(summary.overallSuccess());
    }

    @Test
    void journalContainsCompensationLifecycleEvents() {
        engine.compensate(executionId, List.of(successStep("step-1")), Map.of());

        var events = journal.history(executionId);
        var types = events.stream().map(e -> e.type()).toList();

        assertTrue(types.contains(ExecutionEventType.COMPENSATION_STARTED));
        assertTrue(types.contains(ExecutionEventType.COMPENSATION_ACTION_STARTED));
        assertTrue(types.contains(ExecutionEventType.COMPENSATION_ACTION_COMPLETED));
        assertTrue(types.contains(ExecutionEventType.COMPENSATION_COMPLETED));
    }

    @Test
    void idempotency_secondRunSkipsAlreadyCompensatedSteps() {
        AtomicInteger callCount = new AtomicInteger(0);
        ExecutionStep step = new ExecutionStep() {
            @Override public StepId id() { return StepId.of("idempotent-step"); }
            @Override public boolean isCompensatable() { return true; }
            @Override public StepResult execute(Map<String, Object> ctx) { return StepResult.ok("ok"); }
            @Override public CompensationResult compensate(Map<String, Object> ctx) {
                callCount.incrementAndGet();
                return CompensationResult.compensated();
            }
        };

        // First run
        engine.compensate(executionId, List.of(step), Map.of());
        // Second run (simulating restart after crash mid-compensation)
        engine.compensate(executionId, List.of(step), Map.of());

        assertEquals(1, callCount.get(), "Compensation must be idempotent: step.compensate() called only once");
    }
}
