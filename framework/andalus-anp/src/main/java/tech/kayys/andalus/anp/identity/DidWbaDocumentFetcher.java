package tech.kayys.andalus.anp.identity;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Network boundary for retrieving a DID:WBA document.
 *
 * <p>Implementations must enforce the supplied timeout and response-size
 * limit, reject non-success HTTP responses, avoid forwarding credentials,
 * and apply deployment-specific SSRF / egress controls. The resolver
 * independently validates the returned document's identity and structure.
 */
@FunctionalInterface
public interface DidWbaDocumentFetcher {

    /**
     * Fetches and decodes a DID document from the supplied HTTPS URL.
     *
     * @param documentUrl resolver-generated HTTPS URL
     * @param timeout maximum permitted fetch duration
     * @param maxResponseBytes maximum permitted response body size
     */
    CompletionStage<Optional<DidWbaDocument>> fetch(
            URI documentUrl,
            Duration timeout,
            int maxResponseBytes);
}
