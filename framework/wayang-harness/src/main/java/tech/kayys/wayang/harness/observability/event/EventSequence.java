package tech.kayys.wayang.harness.observability.event;

/**
 * Sequential logical clock sequence for events within an execution.
 */
public record EventSequence(long value) implements Comparable<EventSequence> {
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
