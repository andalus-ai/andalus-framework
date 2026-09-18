package tech.kayys.wayang.harness.security.authorization;

import tech.kayys.wayang.harness.security.capability.CapabilityConstraints;

import java.util.Objects;
import java.util.Optional;

/**
 * Sealed authorization decision outcome.
 */
public sealed interface AuthorizationDecision
        permits AuthorizationDecision.Allowed,
                AuthorizationDecision.Denied,
                AuthorizationDecision.RequiresApproval {

    record Allowed(CapabilityConstraints grantedConstraints, String reason) implements AuthorizationDecision {
        public Allowed {
            grantedConstraints = grantedConstraints != null ? grantedConstraints : CapabilityConstraints.unconstrained();
            reason = reason != null ? reason : "Authorized";
        }
    }

    record Denied(String reason) implements AuthorizationDecision {
        public Denied {
            Objects.requireNonNull(reason, "reason");
        }
    }

    record RequiresApproval(String reason, String approvalAction) implements AuthorizationDecision {
        public RequiresApproval {
            Objects.requireNonNull(reason, "reason");
            approvalAction = approvalAction != null ? approvalAction : "HITL";
        }
    }
}
