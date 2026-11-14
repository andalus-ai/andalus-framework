package tech.kayys.andalus.security.delegation;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Delegation state tracking authority attenuation and provenance across agent hops.
 *
 * <p>The tenant binding is part of the delegated authority, not merely transport
 * metadata. An empty binding represents a legacy or non-tenant-scoped grant and
 * must not be accepted for tenant-scoped delegated authorization.</p>
 */
public record DelegationContext(
        DelegationId id,
        DelegationAudience audience,
        DelegationConstraints constraints,
        Instant issuedAt,
        Instant expiresAt,
        Optional<DelegationId> parent,
        Optional<String> tenantId
) {

    public DelegationContext {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(audience, "audience");
        Objects.requireNonNull(constraints, "constraints");
        Objects.requireNonNull(issuedAt, "issuedAt");
        Objects.requireNonNull(expiresAt, "expiresAt");
        parent = parent == null ? Optional.empty() : parent;
        tenantId = tenantId == null ? Optional.empty() : tenantId;
        tenantId.ifPresent(value -> {
            if (value.isBlank()) {
                throw new IllegalArgumentException("tenantId must not be blank");
            }
        });
    }

    /**
     * Source-compatible constructor for existing callers. Such grants are
     * deliberately unbound and are rejected by tenant-scoped authorization.
     */
    public DelegationContext(
            DelegationId id,
            DelegationAudience audience,
            DelegationConstraints constraints,
            Instant issuedAt,
            Instant expiresAt,
            Optional<DelegationId> parent
    ) {
        this(id, audience, constraints, issuedAt, expiresAt, parent, Optional.empty());
    }

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }

    public static DelegationContext create(DelegationAudience audience, DelegationConstraints constraints, long ttlSeconds) {
        Instant now = Instant.now();
        return new DelegationContext(
                DelegationId.generate(),
                audience,
                constraints,
                now,
                now.plusSeconds(ttlSeconds),
                Optional.empty(),
                Optional.empty()
        );
    }

    public static DelegationContext create(
            DelegationAudience audience,
            DelegationConstraints constraints,
            long ttlSeconds,
            String tenantId
    ) {
        Instant now = Instant.now();
        return new DelegationContext(
                DelegationId.generate(),
                audience,
                constraints,
                now,
                now.plusSeconds(ttlSeconds),
                Optional.empty(),
                Optional.of(Objects.requireNonNull(tenantId, "tenantId"))
        );
    }
}
