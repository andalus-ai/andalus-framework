package tech.kayys.wayang.execution.governance.approval;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Standard implementation of {@link ApprovalService} with strict state transitions and TTL expiration.
 */
public final class DefaultApprovalService implements ApprovalService {

    private final ApprovalStore store;
    private final Clock clock;
    private final Duration defaultTtl;

    public DefaultApprovalService(ApprovalStore store, Clock clock, Duration defaultTtl) {
        this.store = Objects.requireNonNull(store, "store cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
        this.defaultTtl = Objects.requireNonNull(defaultTtl, "defaultTtl cannot be null");

        if (defaultTtl.isNegative() || defaultTtl.isZero()) {
            throw new IllegalArgumentException("defaultTtl must be positive");
        }
    }

    public DefaultApprovalService(ApprovalStore store) {
        this(store, Clock.systemUTC(), Duration.ofMinutes(10));
    }

    @Override
    public ApprovalRequest request(ApprovalRequestTemplate template) {
        Objects.requireNonNull(template, "template cannot be null");

        Instant now = clock.instant();
        Duration ttl = template.ttl() == null ? defaultTtl : template.ttl();
        Instant expiresAt = now.plus(ttl);

        ApprovalRequest request = new ApprovalRequest(
                null,
                template.invocation(),
                template.tenantId(),
                template.userId(),
                template.agentId(),
                template.executionId(),
                template.correlationId(),
                template.reason(),
                now,
                expiresAt,
                ApprovalStatus.PENDING,
                null,
                null,
                null,
                template.metadata()
        );

        store.create(request);
        return request;
    }

    @Override
    public ApprovalRequest get(String approvalId) {
        if (approvalId == null || approvalId.isBlank()) {
            throw new ApprovalNotFoundException("null");
        }
        return store.get(approvalId.trim())
                .orElseThrow(() -> new ApprovalNotFoundException(approvalId));
    }

    @Override
    public ApprovalRequest approve(String approvalId, String approverId, String reason) {
        return transition(approvalId, ApprovalStatus.APPROVED, approverId, reason);
    }

    @Override
    public ApprovalRequest reject(String approvalId, String approverId, String reason) {
        return transition(approvalId, ApprovalStatus.REJECTED, approverId, reason);
    }

    @Override
    public ApprovalRequest cancel(String approvalId, String reason) {
        return transition(approvalId, ApprovalStatus.CANCELLED, null, reason);
    }

    @Override
    public ApprovalRequest expire(String approvalId) {
        return transition(approvalId, ApprovalStatus.EXPIRED, null, "Approval expired");
    }

    @Override
    public boolean isApproved(String approvalId) {
        return get(approvalId).status() == ApprovalStatus.APPROVED;
    }

    private ApprovalRequest transition(String approvalId, ApprovalStatus target, String actor, String reason) {
        ApprovalRequest current = get(approvalId);
        Instant now = clock.instant();

        if (current.pending() && current.expired(now) && target != ApprovalStatus.EXPIRED) {
            ApprovalRequest expired = transition(current, ApprovalStatus.EXPIRED, null, now, "Approval expired");
            store.update(expired);
            throw new ApprovalExpiredException(approvalId);
        }

        ApprovalRequest updated = transition(current, target, actor, now, reason);
        store.update(updated);
        return updated;
    }

    private static ApprovalRequest transition(
            ApprovalRequest current,
            ApprovalStatus target,
            String actor,
            Instant decidedAt,
            String reason) {

        ApprovalStateMachine.transition(current.status(), target);

        return new ApprovalRequest(
                current.id(),
                current.invocation(),
                current.tenantId(),
                current.userId(),
                current.agentId(),
                current.executionId(),
                current.correlationId(),
                current.reason(),
                current.createdAt(),
                current.expiresAt(),
                target,
                actor,
                decidedAt,
                reason,
                current.metadata()
        );
    }
}
