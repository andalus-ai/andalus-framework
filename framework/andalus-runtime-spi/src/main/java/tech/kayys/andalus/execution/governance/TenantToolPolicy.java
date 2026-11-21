package tech.kayys.andalus.execution.governance;

import tech.kayys.andalus.tool.ToolInvocation;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Authorization policy enforcing an explicit tenant allow-list.
 *
 * <p>An empty allow-list is intentionally deny-all. Tenant identifiers in the
 * configured allow-list are trimmed once at construction; caller-supplied
 * tenant identifiers are compared exactly and must already be canonical.</p>
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
        this.allowedTenants = normalizeAllowedTenants(allowedTenants);
        this.priority = priority;
    }

    /**
     * Creates a single-tenant policy. A null tenant retains the historical
     * deny-all behavior of the set-based constructor.
     */
    public TenantToolPolicy(String policyId, String tenantId) {
        this(policyId, tenantId == null ? Set.of() : Set.of(tenantId), 50);
    }

    private static Set<String> normalizeAllowedTenants(Set<String> tenants) {
        if (tenants == null || tenants.isEmpty()) {
            return Set.of();
        }

        Set<String> normalized = new LinkedHashSet<>();
        for (String tenant : tenants) {
            if (tenant == null || tenant.isBlank()) {
                throw new IllegalArgumentException("allowed tenant identifiers cannot be null or blank");
            }
            normalized.add(tenant.trim());
        }
        return Set.copyOf(normalized);
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
        return evaluateTenant(context.tenantId());
    }

    @Override
    public PolicyDecision evaluate(PolicyEvaluationContext context) {
        return evaluateTenant(context.tenantId());
    }

    private PolicyDecision evaluateTenant(String tenantId) {
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
