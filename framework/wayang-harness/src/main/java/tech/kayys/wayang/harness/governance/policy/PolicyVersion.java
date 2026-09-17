package tech.kayys.wayang.harness.governance.policy;

import java.util.Objects;

public record PolicyVersion(
        String policyId,
        String version
) {
    public PolicyVersion {
        Objects.requireNonNull(policyId, "policyId");
        version = version == null ? "1.0" : version;
    }
}
