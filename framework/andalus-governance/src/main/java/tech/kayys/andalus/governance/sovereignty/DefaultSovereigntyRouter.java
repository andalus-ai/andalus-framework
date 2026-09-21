package tech.kayys.andalus.governance.sovereignty;

import java.util.Objects;

/**
 * Standard implementation of {@link SovereigntyRouter}.
 */
public class DefaultSovereigntyRouter implements SovereigntyRouter {

    @Override
    public boolean canDispatch(String workloadTag, SovereigntyPolicy policy, String targetRegion) {
        Objects.requireNonNull(workloadTag, "workloadTag cannot be null");
        Objects.requireNonNull(policy, "policy cannot be null");
        Objects.requireNonNull(targetRegion, "targetRegion cannot be null");

        // If target region matches the required jurisdiction directly, dispatch is allowed
        if (targetRegion.equalsIgnoreCase(policy.requiredJurisdiction())) {
            return true;
        }

        // If policy prohibits cross-border data, reject any non-matching region
        if (policy.prohibitCrossBorderData()) {
            return false;
        }

        // Check if target region is in the explicitly allowed external enclaves
        return policy.allowedExternalEnclaves().contains(targetRegion);
    }
}
