package tech.kayys.wayang.execution.governance;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Sealed result from a {@link ToolPolicy} evaluation.
 * Execution kernel switches on the concrete type or inspects {@link #effect()} to determine the next action.
 */
public sealed interface PolicyDecision
        permits PolicyDecision.Allow, PolicyDecision.Deny, PolicyDecision.RequireApproval {

    PolicyEffect effect();

    PolicyDecisionReason decisionReason();

    String policyId();

    String message();

    String approvalId();

    Map<String, Object> attributes();

    default boolean isAllowed() {
        return effect() == PolicyEffect.ALLOW;
    }

    default boolean isDenied() {
        return effect() == PolicyEffect.DENY;
    }

    default boolean requiresApproval() {
        return effect() == PolicyEffect.REQUIRE_APPROVAL;
    }

    default Optional<String> policyIdOptional() {
        return Optional.ofNullable(policyId());
    }

    default Optional<String> approvalIdOptional() {
        return Optional.ofNullable(approvalId());
    }

    default Optional<String> messageOptional() {
        return Optional.ofNullable(message());
    }

    /**
     * Permitted tool invocation. Proceed with execution.
     */
    record Allow(
            String policyId,
            String message,
            Map<String, Object> attributes
    ) implements PolicyDecision {

        public Allow {
            attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        }

        public Allow() {
            this(null, null, Map.of());
        }

        public Allow(String policyId, String message) {
            this(policyId, message, Map.of());
        }

        @Override
        public PolicyEffect effect() {
            return PolicyEffect.ALLOW;
        }

        @Override
        public PolicyDecisionReason decisionReason() {
            return PolicyDecisionReason.POLICY_ALLOWED;
        }

        @Override
        public String approvalId() {
            return null;
        }
    }

    /**
     * Denied tool invocation. Do not execute; return failure to the agent.
     */
    record Deny(
            String reason,
            String policyId,
            PolicyDecisionReason decisionReason,
            Map<String, Object> attributes
    ) implements PolicyDecision {

        public Deny {
            Objects.requireNonNull(reason, "reason cannot be null");
            decisionReason = decisionReason == null ? PolicyDecisionReason.POLICY_DENIED : decisionReason;
            attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        }

        public Deny(String reason, String policyId) {
            this(reason, policyId, PolicyDecisionReason.POLICY_DENIED, Map.of());
        }

        @Override
        public PolicyEffect effect() {
            return PolicyEffect.DENY;
        }

        @Override
        public String message() {
            return reason;
        }

        @Override
        public String approvalId() {
            return null;
        }
    }

    /**
     * Tool invocation requires explicit approval before executing.
     */
    record RequireApproval(
            String reason,
            String policyId,
            String approvalId,
            Map<String, Object> attributes
    ) implements PolicyDecision {

        public RequireApproval {
            Objects.requireNonNull(reason, "reason cannot be null");
            attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        }

        public RequireApproval(String reason, String policyId) {
            this(reason, policyId, null, Map.of());
        }

        @Override
        public PolicyEffect effect() {
            return PolicyEffect.REQUIRE_APPROVAL;
        }

        @Override
        public PolicyDecisionReason decisionReason() {
            return PolicyDecisionReason.APPROVAL_REQUIRED;
        }

        @Override
        public String message() {
            return reason;
        }
    }

    // --- Convenience factories ---

    static PolicyDecision allow() {
        return new Allow();
    }

    static PolicyDecision allow(String policyId, String message) {
        return new Allow(policyId, message);
    }

    static PolicyDecision deny(String reason, String policyId) {
        return new Deny(reason, policyId);
    }

    static PolicyDecision deny(PolicyDecisionReason reason, String policyId, String message) {
        Objects.requireNonNull(reason, "reason cannot be null");
        if (reason == PolicyDecisionReason.POLICY_ALLOWED) {
            throw new IllegalArgumentException("ALLOW reason cannot be used for a DENY decision");
        }
        return new Deny(message != null ? message : reason.name(), policyId, reason, Map.of());
    }

    static PolicyDecision requireApproval(String reason, String policyId) {
        return new RequireApproval(reason, policyId);
    }

    static PolicyDecision requireApproval(String policyId, String approvalId, String message) {
        return new RequireApproval(message != null ? message : "Approval required", policyId, approvalId, Map.of());
    }

    default PolicyDecision withAttribute(String name, Object value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("attribute name cannot be null or blank");
        }
        var updated = new java.util.HashMap<>(attributes());
        updated.put(name.trim(), value);
        return switch (this) {
            case Allow a -> new Allow(a.policyId(), a.message(), updated);
            case Deny d -> new Deny(d.reason(), d.policyId(), d.decisionReason(), updated);
            case RequireApproval ra -> new RequireApproval(ra.reason(), ra.policyId(), ra.approvalId(), updated);
        };
    }
}
