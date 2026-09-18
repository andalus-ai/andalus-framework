package tech.kayys.wayang.workflow.graph;

/**
 * Execution bounds governing an individual node.
 */
public record NodeConstraints(
        long timeoutMillis,
        int maxRetries,
        boolean requireIsolation
) {

    public static NodeConstraints defaults() {
        return new NodeConstraints(60000L, 2, false);
    }

    public static NodeConstraints isolated() {
        return new NodeConstraints(60000L, 1, true);
    }
}
