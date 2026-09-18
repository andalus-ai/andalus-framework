package tech.kayys.wayang.execution.governance.limits;

import java.time.Instant;

public record LimitCheckResult(
        LimitDecision decision,
        String limitId,
        String reason,
        Instant retryAfter
) {

    public LimitCheckResult {
        if (decision == null) {
            throw new IllegalArgumentException(
                    "decision cannot be null"
            );
        }

        if (limitId == null || limitId.isBlank()) {
            throw new IllegalArgumentException(
                    "limitId cannot be null or blank"
            );
        }

        limitId = limitId.trim();

        reason = reason == null
                ? ""
                : reason.trim();
    }

    public static LimitCheckResult allow(String limitId) {
        return new LimitCheckResult(LimitDecision.ALLOW, limitId, "", null);
    }

    public static LimitCheckResult deny(String limitId, String reason) {
        return new LimitCheckResult(LimitDecision.DENY, limitId, reason, null);
    }

    public static LimitCheckResult deny(String limitId, String reason, Instant retryAfter) {
        return new LimitCheckResult(LimitDecision.DENY, limitId, reason, retryAfter);
    }

    public boolean allowed() {
        return decision == LimitDecision.ALLOW;
    }

    public boolean denied() {
        return decision == LimitDecision.DENY;
    }
}
