package tech.kayys.wayang.execution.governance;

import java.util.Map;

/**
 * Factory utilities for creating standard {@link PolicyDecision} instances.
 */
public final class PolicyDecisions {

    private PolicyDecisions() {
    }

    public static PolicyDecision allow() {
        return PolicyDecision.allow();
    }

    public static PolicyDecision allow(String policyId, String message) {
        return PolicyDecision.allow(policyId, message);
    }

    public static PolicyDecision deny(String policyId, PolicyDecisionReason reason, String message) {
        return PolicyDecision.deny(reason, policyId, message);
    }

    public static PolicyDecision requireApproval(String policyId, String approvalId, String message) {
        return PolicyDecision.requireApproval(policyId, approvalId, message);
    }

    public static PolicyDecision denyPermission(String policyId, String permissionId) {
        return PolicyDecision.deny(
                PolicyDecisionReason.PERMISSION_NOT_GRANTED,
                policyId,
                "Permission not granted: " + permissionId
        ).withAttribute("permissionId", permissionId);
    }

    public static PolicyDecision denyTool(String policyId, String toolName) {
        return PolicyDecision.deny(
                PolicyDecisionReason.TOOL_NOT_ALLOWED,
                policyId,
                "Tool is not allowed: " + toolName
        ).withAttribute("toolName", toolName);
    }

    public static PolicyDecision denyResource(String policyId, String permissionId, String resource) {
        return PolicyDecision.deny(
                PolicyDecisionReason.RESOURCE_NOT_ALLOWED,
                policyId,
                "Resource is outside the allowed scope: " + resource
        ).withAttribute("permissionId", permissionId)
         .withAttribute("resource", resource);
    }
}
