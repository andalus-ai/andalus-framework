package tech.kayys.wayang.workflow.graph;

/**
 * Monotonically increasing version of an execution graph.
 */
public record GraphVersion(long value) {

    public static GraphVersion initial() {
        return new GraphVersion(1L);
    }

    public static GraphVersion of(long value) {
        return new GraphVersion(value);
    }

    public GraphVersion next() {
        return new GraphVersion(value + 1);
    }
}
