package tech.kayys.wayang.harness.consistency.recovery;

import java.util.Map;

/**
 * Unit of execution within a saga-style composite workflow with optional compensation.
 */
public interface ExecutionStep {

    StepId id();

    boolean isCompensatable();

    StepResult execute(Map<String, Object> context);

    CompensationResult compensate(Map<String, Object> context);
}
