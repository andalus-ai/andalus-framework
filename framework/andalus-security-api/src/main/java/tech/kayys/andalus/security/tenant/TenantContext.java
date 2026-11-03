package tech.kayys.andalus.security.tenant;

import java.util.Map;
import java.util.Objects;

/**
 * Multi-tenancy context carried through the Andalus security boundary.
 *
 * <p>A missing tenant is represented only by {@link #empty()}. A present tenant
 * identifier must be non-blank; this prevents malformed identities from
 * silently entering authorization and tenant-isolation decisions.</p>
 */
public record TenantContext(
        String tenantId,
        Map<String, Object> attributes
) {

    public TenantContext {
        if (tenantId != null && tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId must be non-blank when provided");
        }
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    /** Returns a context without a tenant (suitable for system/anonymous calls). */
    public static TenantContext empty() {
        return new TenantContext(null, Map.of());
    }

    /** Creates a tenant-scoped context. */
    public static TenantContext of(String tenantId) {
        Objects.requireNonNull(tenantId, "tenantId");
        if (tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId must not be blank");
        }
        return new TenantContext(tenantId, Map.of());
    }

    public boolean hasTenant() {
        return tenantId != null;
    }
}
