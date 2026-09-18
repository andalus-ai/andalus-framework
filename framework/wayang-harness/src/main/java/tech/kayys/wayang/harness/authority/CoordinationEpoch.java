package tech.kayys.wayang.harness.authority;

/**
 * Monotonically increasing epoch representing leadership and coordination authority generations.
 */
public record CoordinationEpoch(long value) implements Comparable<CoordinationEpoch> {

    public static CoordinationEpoch initial() {
        return new CoordinationEpoch(1L);
    }

    public static CoordinationEpoch of(long value) {
        return new CoordinationEpoch(value);
    }

    public CoordinationEpoch next() {
        return new CoordinationEpoch(value + 1);
    }

    @Override
    public int compareTo(CoordinationEpoch o) {
        return Long.compare(this.value, o.value);
    }
}
