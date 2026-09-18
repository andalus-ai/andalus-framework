package tech.kayys.wayang.governance.policy;

import java.util.Objects;

/**
 * Represents a policy version.
 *
 * <p>Its components capture `policy id`, `version`.</p>
 *
 * @param policyId the policy id
 * @param version the version
 */


public record PolicyVersion(
        String policyId,
        String version
) {
    public PolicyVersion {
        Objects.requireNonNull(policyId, "policyId");
        version = version == null ? "1.0" : version;
    }
}
