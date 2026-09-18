package tech.kayys.wayang.knowledge.snapshot;

import java.util.List;

/**
 * Represents a knowledge snapshot validation.
 *
 * <p>Its components capture `valid`, `missing versions`, `fingerprint mismatches`, `governance mismatches`, `policy mismatches`, and other values.</p>
 *
 * @param valid the valid
 * @param missingVersions the missing versions
 * @param fingerprintMismatches the fingerprint mismatches
 * @param governanceMismatches the governance mismatches
 * @param policyMismatches the policy mismatches
 * @param ruleMismatches the rule mismatches
 */


public record KnowledgeSnapshotValidation(
        boolean valid,
        List<String> missingVersions,
        List<String> fingerprintMismatches,
        List<String> governanceMismatches,
        List<String> policyMismatches,
        List<String> ruleMismatches
) {

    public KnowledgeSnapshotValidation {
        missingVersions = missingVersions == null ? List.of() : List.copyOf(missingVersions);
        fingerprintMismatches = fingerprintMismatches == null ? List.of() : List.copyOf(fingerprintMismatches);
        governanceMismatches = governanceMismatches == null ? List.of() : List.copyOf(governanceMismatches);
        policyMismatches = policyMismatches == null ? List.of() : List.copyOf(policyMismatches);
        ruleMismatches = ruleMismatches == null ? List.of() : List.copyOf(ruleMismatches);
    }
}
