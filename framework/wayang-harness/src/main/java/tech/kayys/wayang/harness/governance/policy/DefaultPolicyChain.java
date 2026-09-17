package tech.kayys.wayang.harness.governance.policy;

import tech.kayys.wayang.harness.governance.action.HarnessAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation of {@link PolicyChain} enforcing decision precedence:
 * DENY > APPROVAL > ALLOW.
 */
public class DefaultPolicyChain implements PolicyChain, HarnessPolicy {

    private final List<HarnessPolicy> policies;

    public DefaultPolicyChain(List<HarnessPolicy> policies) {
        this.policies = policies != null ? List.copyOf(policies) : List.of();
    }

    public static DefaultPolicyChain of(HarnessPolicy... policies) {
        return new DefaultPolicyChain(List.of(policies));
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public PolicyDecision evaluate(PolicyContext context, HarnessAction action) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(action, "action");

        PolicyDecision pendingApproval = null;

        for (HarnessPolicy policy : policies) {
            PolicyDecision decision = policy.evaluate(context, action);
            if (decision instanceof DenyDecision) {
                // Immediate termination: DENY always dominates
                return decision;
            }
            if (decision instanceof ApprovalDecision) {
                if (pendingApproval == null) {
                    pendingApproval = decision;
                }
            }
        }

        if (pendingApproval != null) {
            return pendingApproval;
        }

        return new AllowDecision("policy-chain", "All policies permitted action: " + action.id());
    }

    public static class Builder {
        private final List<HarnessPolicy> policies = new ArrayList<>();

        public Builder add(HarnessPolicy policy) {
            if (policy != null) {
                policies.add(policy);
            }
            return this;
        }

        public DefaultPolicyChain build() {
            return new DefaultPolicyChain(policies);
        }
    }
}
