package tech.kayys.wayang.execution.governance.approval;

import java.time.Instant;

/**
 * Strict state transition engine for {@link ApprovalStatus}.
 */
public final class ApprovalStateMachine {

    private ApprovalStateMachine() {
    }

    public static ApprovalStatus transition(ApprovalStatus current, ApprovalStatus target) {
        if (current == null) {
            throw new IllegalArgumentException("current status cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("target status cannot be null");
        }
        if (current == target) {
            return current;
        }

        if (current != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Cannot transition terminal approval " + current + " to " + target);
        }

        return switch (target) {
            case APPROVED, REJECTED, EXPIRED, CANCELLED -> target;
            case PENDING -> throw new IllegalStateException("PENDING -> PENDING is not a valid transition");
        };
    }

    public static void verifyNotExpired(ApprovalRequest request, Instant now) {
        if (!request.pending()) {
            return;
        }
        if (request.expired(now)) {
            throw new ApprovalExpiredException(request.id());
        }
    }
}
