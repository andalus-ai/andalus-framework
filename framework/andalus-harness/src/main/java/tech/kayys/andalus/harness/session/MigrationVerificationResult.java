package tech.kayys.andalus.harness.session;

import java.io.Serializable;
import java.util.Objects;

/**
 * Result record for session migration verification scenarios.
 */
public record MigrationVerificationResult(
    boolean success,
    String details,
    long recoveredRevision
) implements Serializable {

    public MigrationVerificationResult {
        Objects.requireNonNull(details, "details cannot be null");
    }

    public static MigrationVerificationResult ok(String details, long recoveredRevision) {
        return new MigrationVerificationResult(true, details, recoveredRevision);
    }

    public static MigrationVerificationResult failure(String reason) {
        return new MigrationVerificationResult(false, reason, -1L);
    }
}
