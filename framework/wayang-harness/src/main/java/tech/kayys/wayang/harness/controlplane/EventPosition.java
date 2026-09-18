package tech.kayys.wayang.harness.controlplane;

/**
 * Cursor position within a control-plane event stream.
 */
public record EventPosition(long index) implements Comparable<EventPosition> {

    public static EventPosition start() {
        return new EventPosition(0L);
    }

    public static EventPosition of(long index) {
        return new EventPosition(index);
    }

    public EventPosition next() {
        return new EventPosition(index + 1);
    }

    @Override
    public int compareTo(EventPosition o) {
        return Long.compare(this.index, o.index);
    }
}
