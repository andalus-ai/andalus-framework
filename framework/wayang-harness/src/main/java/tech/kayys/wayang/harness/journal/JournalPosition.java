package tech.kayys.wayang.harness.journal;

/**
 * Monotonically increasing cursor position in an execution journal.
 */
public record JournalPosition(long sequence) implements Comparable<JournalPosition> {

    public static JournalPosition beginning() {
        return new JournalPosition(0L);
    }

    public static JournalPosition of(long sequence) {
        return new JournalPosition(sequence);
    }

    public JournalPosition next() {
        return new JournalPosition(sequence + 1);
    }

    @Override
    public int compareTo(JournalPosition o) {
        return Long.compare(this.sequence, o.sequence);
    }
}
