package tech.kayys.wayang.harness.memory;

import java.util.Objects;
import java.util.UUID;

public record MemoryId(String value) {
    public MemoryId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Memory id must not be blank");
        }
    }

    public static MemoryId of(String value) {
        return new MemoryId(value);
    }

    public static MemoryId generate() {
        return new MemoryId("mem-" + UUID.randomUUID());
    }
}
