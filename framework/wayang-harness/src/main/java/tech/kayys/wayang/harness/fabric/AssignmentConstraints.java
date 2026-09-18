package tech.kayys.wayang.harness.fabric;

/**
 * Execution bounds governing an assignment.
 */
public record AssignmentConstraints(
        long timeoutMillis,
        int maxRetries
) {

    public static AssignmentConstraints defaults() {
        return new AssignmentConstraints(60000L, 2);
    }
}
