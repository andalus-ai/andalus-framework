package tech.kayys.wayang.execution.secrets;

import tech.kayys.wayang.execution.sandbox.SandboxContext;

import java.util.Optional;

/**
 * Provider interface for securely retrieving secret values at enforcement points.
 */
public interface SecretProvider {

    Optional<String> resolveSecret(SecretId id, SandboxContext context);
}
