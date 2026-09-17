package tech.kayys.wayang.harness.governance.approval;

import tech.kayys.wayang.harness.governance.action.HarnessAction;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record ApprovalRequest(
        ApprovalId id,
        HarnessAction action,
        String reason,
        Map<String, Object> evidence,
        Instant expiresAt
) {
    public ApprovalRequest {
        id = id == null ? ApprovalId.generate() : id;
        Objects.requireNonNull(action, "action");
        reason = reason == null ? "" : reason;
        evidence = evidence == null ? Map.of() : Map.copyOf(evidence);
        expiresAt = expiresAt == null ? Instant.now().plusSeconds(3600) : expiresAt;
    }

    public static ApprovalRequest of(HarnessAction action, String reason) {
        return new ApprovalRequest(ApprovalId.generate(), action, reason, Map.of(), Instant.now().plusSeconds(3600));
    }
}
