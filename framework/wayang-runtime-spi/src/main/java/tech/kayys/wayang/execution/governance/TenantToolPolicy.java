package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolInvocation;

import java.util.Objects;
import java.util.Set;

/**
 * Authorization policy enforcing tenant boundary.
 */
public final class TenantToolPolicy implements AuthorizationPolicy {

    private final String policyId;
    private final Set<String> allowedTenants;
    private final int priority;

    public TenantToolPolicy(String policyId, Set<String> allowedTenants, int priority) {
        if (policyId == null || policyId.isBlank()) {
            throw new IllegalArgumentException("policyId cannot be null or blank");
        }
        this.policyId = policyId.trim();
        this.allowedTenants = allowedTenants == null ? Set.of() : Set.copyOf(allowedTenants);
        this.priority = priority;
    }

    public TenantToolPolicy(String policyId, String tenantId) {
        this(policyId, tenantId == null ? Set.of() : Set.of(tenantId), 50);
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
        return AuthorizationScope.TENANT;
    }

    @Override
    public PolicyDecision evaluate(ToolInvocation invocation, ToolPermissionContext context) {
        String tenantId = context.tenantId();
        if (tenantId == null || !allowedTenants.contains(tenantId)) {
            return PolicyDecision.deny(
                    PolicyDecisionReason.TENANT_NOT_AUTHORIZED,
                    policyId,
                    "Tenant is not authorized for this tool: " + (tenantId != null ? tenantId : "anonymous")
            );
        }
        return PolicyDecision.allow(policyId, "Tenant authorized");
    }

    @Override
    public PolicyDecision evaluate(PolicyEvaluationContext context) {
        String tenantId = context.tenantId();
        if (tenantId == null || !allowedTenants.contains(tenantId)) {
            return PolicyDecision.deny(
                    PolicyDecisionReason.TENANT_NOT_AUTHORIZED,
                    policyId,
                    "Tenant is not authorized for this tool: " + (tenantId != null ? tenantId : "anonymous")
            );
        }
        return PolicyDecision.allow(policyId, "Tenant authorized");
    }
}
