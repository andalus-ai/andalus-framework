package tech.kayys.wayang.harness.journal;

/**
 * Strict monotonic ordering sequence assigned to events within an execution stream.
 */
public record EventSequence(long value) implements Comparable<EventSequence> {

    public static EventSequence initial() {
        return new EventSequence(1L);
    }

    public static EventSequence of(long value) {
        return new EventSequence(value);
    }

    public EventSequence next() {
        return new EventSequence(value + 1);
    }

    @Override
    public int compareTo(EventSequence o) {
        return Long.compare(this.value, o.value);
    }
}
