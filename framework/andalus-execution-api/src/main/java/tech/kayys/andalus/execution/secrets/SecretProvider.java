package tech.kayys.andalus.execution.secrets;

import tech.kayys.andalus.execution.sandbox.SandboxContext;

import java.util.Optional;

/**
 * Provider interface for securely retrieving secret values at enforcement points.
 */
public interface SecretProvider {

    Optional<String> resolveSecret(SecretId id, SandboxContext context);
}
