package tech.kayys.wayang.state.retention;

import java.time.Instant;
import java.util.Objects;

public record RetentionDecision(
        RetentionStatus status,
        Instant actionTime,
        boolean deletePayload
) {
    public RetentionDecision {
        Objects.requireNonNull(status, "status cannot be null");
    }

    public static RetentionDecision keep(RetentionStatus status) {
        return new RetentionDecision(status, Instant.now(), false);
    }

    public static RetentionDecision expire() {
        return new RetentionDecision(RetentionStatus.EXPIRED, Instant.now(), true);
    }
}
