package tech.kayys.wayang.execution.portability;

import java.util.Objects;

/**
 * First-class portable execution bundle containing manifest, digest, and serialized payload.
 */
public record PortableExecution(
    ExecutionManifest manifest,
    PackageDigest digest,
    byte[] payload
) {
    public PortableExecution {
        Objects.requireNonNull(manifest, "manifest cannot be null");
        Objects.requireNonNull(digest, "digest cannot be null");
        payload = payload != null ? payload.clone() : new byte[0];
    }

    @Override
    public byte[] payload() {
        return payload.clone();
    }
}
