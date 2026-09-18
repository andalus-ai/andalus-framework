package tech.kayys.wayang.workflow.graph;

/**
 * Bounds and execution limits for an entire execution graph.
 */
public record GraphConstraints(
        long maxDurationMillis,
        int maxParallelNodes,
        boolean requireAllComplete
) {

    public static GraphConstraints defaults() {
        return new GraphConstraints(300000L, 10, true);
    }

    public static GraphConstraints unconstrained() {
        return new GraphConstraints(Long.MAX_VALUE, Integer.MAX_VALUE, false);
    }
}
