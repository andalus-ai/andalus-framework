package tech.kayys.andalus.execution.fabric;

/**
 * Epoch generation counter disambiguating node restarts.
 */
public record NodeGeneration(long value) {

    public static NodeGeneration initial() {
        return new NodeGeneration(1L);
    }

    public static NodeGeneration of(long value) {
        return new NodeGeneration(value);
    }

    public NodeGeneration next() {
        return new NodeGeneration(value + 1);
    }
}
