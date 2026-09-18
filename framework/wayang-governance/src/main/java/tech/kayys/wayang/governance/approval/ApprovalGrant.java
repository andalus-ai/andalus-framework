package tech.kayys.wayang.governance.approval;

import java.time.Instant;
import java.util.Objects;

/**
 * Scoped authorization grant token proving valid approval for a specific action.
 */
public record ApprovalGrant(
        ApprovalId approvalId,
        String actionId,
        Instant issuedAt,
        Instant expiresAt,
        boolean valid
) {
    public ApprovalGrant {
        Objects.requireNonNull(approvalId, "approvalId");
        Objects.requireNonNull(actionId, "actionId");
        issuedAt = issuedAt == null ? Instant.now() : issuedAt;
        expiresAt = expiresAt == null ? issuedAt.plusSeconds(3600) : expiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return valid && !isExpired();
    }
}
