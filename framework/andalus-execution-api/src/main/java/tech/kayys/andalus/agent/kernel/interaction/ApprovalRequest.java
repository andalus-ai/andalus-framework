
package tech.kayys.andalus.agent.kernel.interaction;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Request for human authorization during an AgentRun.
 *
 * <p>An approval request is deliberately independent from A2UI.
 * A2UI, CLI, API, or another interaction protocol may render it.</p>
 */
public record ApprovalRequest(
        String id,
        String runId,
        String reason,
        String capability,
        Map<String, Object> context,
        Instant createdAt,
        Instant expiresAt,
        ApprovalStatus status
) {

    public ApprovalRequest {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(runId, "runId");
        Objects.requireNonNull(reason, "reason");

        context = context == null
                ? Map.of()
                : Map.copyOf(context);

        createdAt = createdAt == null
                ? Instant.now()
                : createdAt;

        status = status == null
                ? ApprovalStatus.PENDING
                : status;
    }

    public ApprovalRequest(
            String id,
            String runId,
            String reason,
            String capability,
            Map<String, Object> context
    ) {
        this(
                id,
                runId,
                reason,
                capability,
                context,
                Instant.now(),
                null,
                ApprovalStatus.PENDING
        );
    }

    public boolean pending() {
        return status == ApprovalStatus.PENDING;
    }

    public boolean expired() {
        return expiresAt != null
                && Instant.now().isAfter(expiresAt);
    }
}
