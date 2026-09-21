package tech.kayys.andalus.governance.approval;

import java.time.Instant;
import java.util.Objects;

/**
 * Durable human-in-the-loop approval reference for session operations and capability escalation.
 */
public record ApprovalReference(
        ApprovalId id,
        String approverPrincipalId,
        String scope,
        Instant issuedAt,
        Instant expiresAt) {

    public ApprovalReference {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(approverPrincipalId, "approverPrincipalId must not be null");
        scope = scope != null ? scope : "default";
        issuedAt = issuedAt != null ? issuedAt : Instant.now();
        expiresAt = expiresAt != null ? expiresAt : issuedAt.plusSeconds(3600);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public static ApprovalReference of(ApprovalId id, String approverPrincipalId, String scope) {
        Instant now = Instant.now();
        return new ApprovalReference(id, approverPrincipalId, scope, now, now.plusSeconds(3600));
    }
}
