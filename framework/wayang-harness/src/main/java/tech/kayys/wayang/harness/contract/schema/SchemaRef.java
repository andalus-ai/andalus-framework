package tech.kayys.wayang.harness.contract.schema;

import java.util.Objects;

/**
 * Machine-readable schema reference without requiring Java class sharing.
 */
public record SchemaRef(String id, String version) {
    public SchemaRef {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(version, "version cannot be null");
    }

    public static SchemaRef of(String id, String version) {
        return new SchemaRef(id, version);
    }
}
