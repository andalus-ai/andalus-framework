package tech.kayys.wayang.knowledge.integrity;

/**
 * Represents a knowledge snapshot integrity policy.
 *
 * <p>Its components capture `verify knowledge`, `verify policies`, `verify rules`, `verify governance`, `verify runtime`, and other values.</p>
 *
 * @param verifyKnowledge the verify knowledge
 * @param verifyPolicies the verify policies
 * @param verifyRules the verify rules
 * @param verifyGovernance the verify governance
 * @param verifyRuntime the verify runtime
 * @param verifyLineage the verify lineage
 * @param verifyDependencies the verify dependencies
 * @param failOnMissingDependency the fail on missing dependency
 */


public record KnowledgeSnapshotIntegrityPolicy(
        boolean verifyKnowledge,
        boolean verifyPolicies,
        boolean verifyRules,
        boolean verifyGovernance,
        boolean verifyRuntime,
        boolean verifyLineage,
        boolean verifyDependencies,
        boolean failOnMissingDependency
) {

    public static KnowledgeSnapshotIntegrityPolicy strict() {
        return new KnowledgeSnapshotIntegrityPolicy(true, true, true, true, true, true, true, true);
    }

    public static KnowledgeSnapshotIntegrityPolicy relaxed() {
        return new KnowledgeSnapshotIntegrityPolicy(true, true, true, true, false, false, true, false);
    }
}
