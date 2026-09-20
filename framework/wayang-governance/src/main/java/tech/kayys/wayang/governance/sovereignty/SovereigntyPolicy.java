package tech.kayys.wayang.governance.sovereignty;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * Data sovereignty policy specifying geographical, jurisdictional, and enclave constraints.
 */
public record SovereigntyPolicy(
    String policyId,
    String requiredJurisdiction,
    boolean prohibitCrossBorderData,
    Set<String> allowedExternalEnclaves
) implements Serializable {

    public SovereigntyPolicy {
        Objects.requireNonNull(policyId, "policyId cannot be null");
        Objects.requireNonNull(requiredJurisdiction, "requiredJurisdiction cannot be null");
        allowedExternalEnclaves = allowedExternalEnclaves != null ? Set.copyOf(allowedExternalEnclaves) : Set.of();
    }

    public static SovereigntyPolicy strict(String policyId, String requiredJurisdiction) {
        return new SovereigntyPolicy(policyId, requiredJurisdiction, true, Set.of());
    }

    public static SovereigntyPolicy permissive(String policyId, String requiredJurisdiction, Set<String> allowedEnclaves) {
        return new SovereigntyPolicy(policyId, requiredJurisdiction, false, allowedEnclaves);
    }
}
