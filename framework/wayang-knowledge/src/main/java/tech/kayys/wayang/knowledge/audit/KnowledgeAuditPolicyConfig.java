package tech.kayys.wayang.knowledge.audit;

/**
 * Represents a knowledge audit policy config.
 *
 * <p>Its components capture `failure mode`, `require tenant`, `require execution id`, `require agent id`.</p>
 *
 * @param failureMode the failure mode
 * @param requireTenant the require tenant
 * @param requireExecutionId the require execution id
 * @param requireAgentId the require agent id
 */


public record KnowledgeAuditPolicyConfig(
        KnowledgeAuditFailureMode failureMode,
        boolean requireTenant,
        boolean requireExecutionId,
        boolean requireAgentId
) {

    public static KnowledgeAuditPolicyConfig strict() {
        return new KnowledgeAuditPolicyConfig(
                KnowledgeAuditFailureMode.FAIL_CLOSED,
                true,
                true,
                true
        );
    }

    public static KnowledgeAuditPolicyConfig relaxed() {
        return new KnowledgeAuditPolicyConfig(
                KnowledgeAuditFailureMode.FAIL_OPEN,
                true,
                false,
                false
        );
    }
}
