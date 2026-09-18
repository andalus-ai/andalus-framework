package tech.kayys.wayang.harness.environment.v3.secret;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.util.Objects;

/**
 * Access request for obtaining a secret lease.
 */
public record SecretAccessRequest(
        ExecutionId executionId,
        String purpose
) {
    public SecretAccessRequest {
        Objects.requireNonNull(executionId, "executionId");
        purpose = purpose != null ? purpose : "execution";
    }

    public static SecretAccessRequest of(ExecutionId executionId) {
        return new SecretAccessRequest(executionId, "execution");
    }
}
