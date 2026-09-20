package tech.kayys.wayang.execution.fabric;

import java.util.Objects;
import java.util.Set;

/**
 * Trust boundary for federated execution fabric nodes.
 */
public record TrustDomain(
    String domainId,
    String authorityUrl,
    Set<String> trustedNamespaces
) {
    public TrustDomain {
        Objects.requireNonNull(domainId, "domainId cannot be null");
        Objects.requireNonNull(authorityUrl, "authorityUrl cannot be null");
        trustedNamespaces = trustedNamespaces != null ? Set.copyOf(trustedNamespaces) : Set.of();
    }

    public static TrustDomain local() {
        return new TrustDomain("local-domain", "http://localhost:8080", Set.of("default", "wayang"));
    }
}
