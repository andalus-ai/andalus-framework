package tech.kayys.wayang.harness.controlplane;

/**
 * Semantic version of an event payload schema.
 */
public record EventVersion(int major, int minor) {

    public static EventVersion initial() {
        return new EventVersion(1, 0);
    }

    public static EventVersion of(int major, int minor) {
        return new EventVersion(major, minor);
    }
}
