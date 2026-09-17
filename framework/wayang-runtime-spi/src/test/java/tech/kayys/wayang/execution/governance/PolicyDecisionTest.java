package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PolicyDecisionTest {

    @Test
    void allowDecisionIsAllowed() {
        PolicyDecision decision = PolicyDecision.allow();
        assertEquals(PolicyEffect.ALLOW, decision.effect());
        assertTrue(decision.isAllowed());
        assertFalse(decision.isDenied());
        assertFalse(decision.requiresApproval());
        assertNull(decision.approvalId());
    }

    @Test
    void allowWithPolicyId() {
        PolicyDecision decision = PolicyDecision.allow("my-policy", "Allowed by rule");
        assertEquals(PolicyEffect.ALLOW, decision.effect());
        assertTrue(decision.isAllowed());
        assertEquals("my-policy", decision.policyId());
        assertEquals("Allowed by rule", decision.message());
    }

    @Test
    void denyDecisionIsDenied() {
        PolicyDecision decision = PolicyDecision.deny("Write denied", "filesystem-policy");
        assertTrue(decision.isDenied());
        assertFalse(decision.isAllowed());
        assertFalse(decision.requiresApproval());
        assertEquals("filesystem-policy", decision.policyId());
    }

    @Test
    void denyWithReason() {
        PolicyDecision decision = PolicyDecision.deny(
                PolicyDecisionReason.PERMISSION_NOT_GRANTED,
                "filesystem-policy",
                "Write permission required"
        );
        assertTrue(decision.isDenied());
        assertEquals("filesystem-policy", decision.policyId());
        assertEquals(PolicyDecisionReason.PERMISSION_NOT_GRANTED, decision.decisionReason());
    }

    @Test
    void approvalDecisionIsNotAllowedOrDenied() {
        PolicyDecision decision = PolicyDecision.requireApproval("approval-needed", "approval-policy");
        assertTrue(decision.requiresApproval());
        assertFalse(decision.isAllowed());
        assertFalse(decision.isDenied());
        assertEquals("approval-policy", decision.policyId());
    }

    @Test
    void approvalWithApprovalId() {
        PolicyDecision decision = PolicyDecision.requireApproval("approval-policy", "appr-123", "Human review required");
        assertTrue(decision.requiresApproval());
        assertEquals("appr-123", decision.approvalId());
        assertEquals("Human review required", decision.message());
        assertEquals(PolicyDecisionReason.APPROVAL_REQUIRED, decision.decisionReason());
    }

    @Test
    void withAttributeIsImmutable() {
        PolicyDecision original = PolicyDecision.allow();
        PolicyDecision withAttr = original.withAttribute("rule", "workspace-read");

        assertNull(original.attributes().get("rule"),
                "Original must not be mutated");
        assertEquals("workspace-read", withAttr.attributes().get("rule"));
    }

    @Test
    void attributeMapIsUnmodifiable() {
        PolicyDecision decision = PolicyDecision.allow().withAttribute("rule", "value");
        assertThrows(UnsupportedOperationException.class,
                () -> decision.attributes().put("another", "x"));
    }

    @Test
    void denyWithAllowReasonThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                PolicyDecision.deny(PolicyDecisionReason.POLICY_ALLOWED, "p", "message"));
    }
}
