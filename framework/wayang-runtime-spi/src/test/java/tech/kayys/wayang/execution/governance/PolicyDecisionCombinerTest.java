package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PolicyDecisionCombinerTest {

    @Test
    void allowAndAllowProducesAllow() {
        PolicyDecision result = PolicyDecisionCombiner.combine(
                PolicyDecision.allow(),
                PolicyDecision.allow()
        );
        assertTrue(result.isAllowed());
    }

    @Test
    void allowAndApprovalProducesApproval() {
        PolicyDecision result = PolicyDecisionCombiner.combine(
                PolicyDecision.allow(),
                PolicyDecision.requireApproval("approval required", "approval-policy")
        );
        assertTrue(result.requiresApproval());
    }

    @Test
    void approvalAndAllowRemainsApproval() {
        PolicyDecision result = PolicyDecisionCombiner.combine(
                PolicyDecision.requireApproval("approval required", "approval-policy"),
                PolicyDecision.allow()
        );
        assertTrue(result.requiresApproval());
    }

    @Test
    void allowAndDenyProducesDeny() {
        PolicyDecision result = PolicyDecisionCombiner.combine(
                PolicyDecision.allow(),
                PolicyDecision.deny("forbidden", "deny-policy")
        );
        assertTrue(result.isDenied());
    }

    @Test
    void approvalAndDenyProducesDeny() {
        PolicyDecision result = PolicyDecisionCombiner.combine(
                PolicyDecision.requireApproval("approval required", "approval-policy"),
                PolicyDecision.deny("forbidden", "deny-policy")
        );
        assertTrue(result.isDenied());
    }

    @Test
    void denyAndAllowKeepsOriginalDeny() {
        PolicyDecision deny = PolicyDecision.deny("forbidden", "deny-policy");
        PolicyDecision result = PolicyDecisionCombiner.combine(
                deny,
                PolicyDecision.allow()
        );
        assertSame(deny, result);
    }

    @Test
    void denyAndApprovalKeepsOriginalDeny() {
        PolicyDecision deny = PolicyDecision.deny("forbidden", "deny-policy");
        PolicyDecision result = PolicyDecisionCombiner.combine(
                deny,
                PolicyDecision.requireApproval("approval required", "approval-policy")
        );
        assertSame(deny, result);
    }
}
