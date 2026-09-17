package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolInvocation;

import java.util.Objects;
import java.util.Set;

/**
 * Authorization policy requiring the caller to hold at least one of the specified roles.
 */
public final class RoleToolPolicy implements AuthorizationPolicy {

    private final String policyId;
    private final Set<String> requiredRoles;
    private final int priority;

    public RoleToolPolicy(String policyId, Set<String> requiredRoles, int priority) {
        if (policyId == null || policyId.isBlank()) {
            throw new IllegalArgumentException("policyId cannot be null or blank");
        }
        this.policyId = policyId.trim();
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            throw new IllegalArgumentException("requiredRoles cannot be empty");
        }
        this.requiredRoles = Set.copyOf(requiredRoles);
        this.priority = priority;
    }

    public RoleToolPolicy(String policyId, Set<String> requiredRoles) {
        this(policyId, requiredRoles, 100);
    }

    @Override
    public String id() {
        return policyId;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public AuthorizationScope scope() {
        return AuthorizationScope.ROLE;
    }

    @Override
    public PolicyDecision evaluate(ToolInvocation invocation, ToolPermissionContext context) {
        if (context.roles() == null || context.roles().isEmpty()) {
            return PolicyDecision.deny(
                    PolicyDecisionReason.ROLE_NOT_AUTHORIZED,
                    policyId,
                    "Required role is missing"
            );
        }

        boolean authorized = context.roles().stream().anyMatch(requiredRoles::contains);
        if (!authorized) {
            return PolicyDecision.deny(
                    PolicyDecisionReason.ROLE_NOT_AUTHORIZED,
                    policyId,
                    "Required role is missing"
            );
        }

        return PolicyDecision.allow(policyId, "Role authorized");
    }

    @Override
    public PolicyDecision evaluate(PolicyEvaluationContext context) {
        if (context.roles() == null || context.roles().isEmpty()) {
            return PolicyDecision.deny(
                    PolicyDecisionReason.ROLE_NOT_AUTHORIZED,
                    policyId,
                    "Required role is missing"
            );
        }

        boolean authorized = context.roles().stream().anyMatch(requiredRoles::contains);
        if (!authorized) {
            return PolicyDecision.deny(
                    PolicyDecisionReason.ROLE_NOT_AUTHORIZED,
                    policyId,
                    "Required role is missing"
            );
        }

        return PolicyDecision.allow(policyId, "Role authorized");
    }
}
