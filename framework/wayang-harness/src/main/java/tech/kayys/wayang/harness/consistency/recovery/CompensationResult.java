package tech.kayys.wayang.harness.consistency.recovery;

/**
 * Result of compensating an execution step.
 */
public record CompensationResult(
        boolean success,
        String message
) {

    public CompensationResult {
        message = message != null ? message : "";
    }

    public static CompensationResult compensated() {
        return new CompensationResult(true, "Compensated successfully");
    }

    public static CompensationResult nonCompensatable(String reason) {
        return new CompensationResult(false, reason);
    }

    public static CompensationResult failed(String reason) {
        return new CompensationResult(false, reason);
    }
}
