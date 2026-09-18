package tech.kayys.wayang.harness.consistency.recovery;

import java.util.Optional;

/**
 * Result of executing an execution step.
 */
public record StepResult(
        boolean success,
        Optional<Object> output,
        String error
) {

    public StepResult {
        output = output != null ? output : Optional.empty();
        error = error != null ? error : "";
    }

    public static StepResult ok(Object output) {
        return new StepResult(true, Optional.ofNullable(output), "");
    }

    public static StepResult failed(String error) {
        return new StepResult(false, Optional.empty(), error);
    }
}
