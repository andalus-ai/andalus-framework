package tech.kayys.wayang.execution.fabric;

import java.util.Objects;

/**
 * Identity of a federation connecting multiple autonomous Wayang fabric clusters.
 */
public record FederationIdentity(
    String federationId,
    TrustDomain trustDomain
) {
    public FederationIdentity {
        Objects.requireNonNull(federationId, "federationId cannot be null");
        Objects.requireNonNull(trustDomain, "trustDomain cannot be null");
    }
}
