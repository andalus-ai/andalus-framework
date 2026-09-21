package tech.kayys.andalus.security.authn;

import java.util.concurrent.CompletionStage;

/**
 * SPI for authentication.
 * Implementations handle specific schemes (Bearer/JWT, ApiKey, DID, etc.)
 * without exposing protocol details to Andalus core.
 */
public interface AuthenticationService {

    CompletionStage<AuthenticationResult> authenticate(AuthenticationRequest request);
}
