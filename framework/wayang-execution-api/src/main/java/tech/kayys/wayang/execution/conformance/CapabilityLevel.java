package tech.kayys.wayang.execution.conformance;

/**
 * Defined contract tier for a capability.
 */
public enum CapabilityLevel {
    NONE(0),
    BASIC(1),
    STANDARD(2),
    ADVANCED(3),
    FULL(4);

    private final int level;

    CapabilityLevel(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }

    public boolean satisfies(CapabilityLevel minimum) {
        if (minimum == null) return true;
        return this.level >= minimum.level;
    }
}
