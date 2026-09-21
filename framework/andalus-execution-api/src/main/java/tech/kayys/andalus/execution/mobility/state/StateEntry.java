package tech.kayys.andalus.execution.mobility.state;

import java.util.Objects;

/**
 * An individual entry within an execution state manifest.
 */
public record StateEntry(
        String key,
        String type,
        String digest,
        long size,
        StateDurability durability
) {
    public StateEntry {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(digest, "digest cannot be null");
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be negative");
        }
        if (durability == null) {
            durability = StateDurability.DURABLE;
        }
    }
}
