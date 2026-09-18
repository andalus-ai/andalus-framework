package tech.kayys.wayang.harness.authority;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Point-in-time view of active cluster membership and current leader.
 */
public record MembershipView(
        CoordinationEpoch epoch,
        Set<CoordinatorId> activeCoordinators,
        Optional<CoordinatorId> leaderId
) {

    public MembershipView {
        Objects.requireNonNull(epoch, "CoordinationEpoch cannot be null");
        activeCoordinators = activeCoordinators != null ? Set.copyOf(activeCoordinators) : Set.of();
        leaderId = leaderId != null ? leaderId : Optional.empty();
    }

    public static MembershipView of(CoordinationEpoch epoch, Set<CoordinatorId> coordinators, CoordinatorId leader) {
        return new MembershipView(epoch, coordinators, Optional.ofNullable(leader));
    }
}
