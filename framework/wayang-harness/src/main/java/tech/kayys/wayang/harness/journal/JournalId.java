package tech.kayys.wayang.harness.journal;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for an execution journal.
 */
public record JournalId(String value) {

    public JournalId {
        Objects.requireNonNull(value, "JournalId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("JournalId value cannot be blank");
        }
    }

    public static JournalId of(String value) {
        return new JournalId(value);
    }

    public static JournalId generate() {
        return new JournalId("journal-" + UUID.randomUUID());
    }
}
