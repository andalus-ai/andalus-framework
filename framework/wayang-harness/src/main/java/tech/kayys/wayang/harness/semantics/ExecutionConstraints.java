package tech.kayys.wayang.harness.semantics;

/**
 * Execution bounds and constraints governing an operation.
 */
public record ExecutionConstraints(
        long timeoutMillis,
        int maxRetries,
        boolean requireSandbox
) {

    public static ExecutionConstraints defaults() {
        return new ExecutionConstraints(30000L, 3, false);
    }

    public static ExecutionConstraints sandboxed() {
        return new ExecutionConstraints(30000L, 1, true);
    }
}
