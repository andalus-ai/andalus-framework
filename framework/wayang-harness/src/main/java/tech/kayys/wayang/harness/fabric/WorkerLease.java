package tech.kayys.wayang.harness.fabric;

import tech.kayys.wayang.harness.isolation.LeaseId;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Time-bounded lease granted to a worker establishing its membership and authority to execute.
 */
public record WorkerLease(
        LeaseId id,
        Instant acquiredAt,
        Instant expiresAt,
        boolean active
) {

    public WorkerLease {
        Objects.requireNonNull(id, "LeaseId cannot be null");
        acquiredAt = acquiredAt != null ? acquiredAt : Instant.now();
        expiresAt = expiresAt != null ? expiresAt : acquiredAt.plus(Duration.ofMinutes(5));
    }

    public static WorkerLease create(Duration duration) {
        Instant now = Instant.now();
        return new WorkerLease(
                LeaseId.generate(),
                now,
                now.plus(duration != null ? duration : Duration.ofMinutes(5)),
                true
        );
    }

    public WorkerLease renew(Duration duration) {
        return new WorkerLease(
                id,
                acquiredAt,
                Instant.now().plus(duration != null ? duration : Duration.ofMinutes(5)),
                true
        );
    }

    public WorkerLease expire() {
        return new WorkerLease(id, acquiredAt, Instant.now(), false);
    }

    public boolean isExpired() {
        return !active || Instant.now().isAfter(expiresAt);
    }
}
