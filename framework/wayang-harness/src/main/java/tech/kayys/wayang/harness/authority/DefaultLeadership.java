package tech.kayys.wayang.harness.authority;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Immutable reference record implementing {@link Leadership}.
 */
public record DefaultLeadership(
        LeadershipId id,
        CoordinatorId coordinator,
        CoordinationEpoch epoch,
        Instant acquiredAt,
        Instant expiresAt
) implements Leadership {

    public DefaultLeadership {
        Objects.requireNonNull(id, "LeadershipId cannot be null");
        Objects.requireNonNull(coordinator, "CoordinatorId cannot be null");
        Objects.requireNonNull(epoch, "CoordinationEpoch cannot be null");
        acquiredAt = acquiredAt != null ? acquiredAt : Instant.now();
        expiresAt = expiresAt != null ? expiresAt : acquiredAt.plus(Duration.ofMinutes(1));
    }

    public static DefaultLeadership of(
            CoordinatorId coordinator,
            CoordinationEpoch epoch,
            Duration leaseDuration
    ) {
        Instant now = Instant.now();
        return new DefaultLeadership(
                LeadershipId.generate(),
                coordinator,
                epoch,
                now,
                now.plus(leaseDuration != null ? leaseDuration : Duration.ofMinutes(1))
        );
    }

    @Override
    public boolean isValid() {
        return Instant.now().isBefore(expiresAt);
    }
}
