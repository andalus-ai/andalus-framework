
package tech.kayys.andalus.agent.kernel.interaction;

import java.time.Instant;
import java.util.Map;

public record ApprovalDecision(
        String approvalId,
        String runId,
        ApprovalStatus status,
        String actor,
        String reason,
        Map<String, Object> metadata,
        Instant decidedAt
) {

    public ApprovalDecision {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);

        decidedAt = decidedAt == null
                ? Instant.now()
                : decidedAt;
    }

    public static ApprovalDecision granted(
            String approvalId,
            String runId,
            String actor
    ) {
        return new ApprovalDecision(
                approvalId,
                runId,
                ApprovalStatus.GRANTED,
                actor,
                null,
                Map.of(),
                Instant.now()
        );
    }

    public static ApprovalDecision rejected(
            String approvalId,
            String runId,
            String actor,
            String reason
    ) {
        return new ApprovalDecision(
                approvalId,
                runId,
                ApprovalStatus.REJECTED,
                actor,
                reason,
                Map.of(),
                Instant.now()
        );
    }
}
