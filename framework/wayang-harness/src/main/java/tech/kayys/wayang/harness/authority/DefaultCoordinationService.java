package tech.kayys.wayang.harness.authority;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Standard in-memory reference implementation of {@link CoordinationService} and {@link LeadershipElection}.
 */
public class DefaultCoordinationService implements CoordinationService, LeadershipElection {

    private final CoordinationIdentity identity;
    private final AtomicReference<LeadershipState> state = new AtomicReference<>(LeadershipState.STARTING);
    private final AtomicReference<DefaultLeadership> currentLeadership = new AtomicReference<>();
    private final AtomicReference<CoordinationEpoch> currentEpoch = new AtomicReference<>(CoordinationEpoch.initial());
    private final Set<CoordinatorId> members = ConcurrentHashMap.newKeySet();

    public DefaultCoordinationService(CoordinationIdentity identity) {
        this.identity = Objects.requireNonNull(identity, "CoordinationIdentity cannot be null");
        this.members.add(identity.coordinatorId());
        this.state.set(LeadershipState.FOLLOWER);
    }

    @Override
    public CoordinationIdentity identity() {
        return identity;
    }

    @Override
    public LeadershipState state() {
        return state.get();
    }

    @Override
    public Optional<Leadership> leadership() {
        DefaultLeadership lead = currentLeadership.get();
        if (lead != null && lead.isValid()) {
            return Optional.of(lead);
        }
        return Optional.empty();
    }

    @Override
    public LeadershipElection election() {
        return this;
    }

    @Override
    public MembershipView membership() {
        CoordinatorId leaderId = currentLeadership.get() != null ? currentLeadership.get().coordinator() : null;
        return new MembershipView(currentEpoch.get(), Set.copyOf(members), Optional.ofNullable(leaderId));
    }

    @Override
    public boolean validateAuthority(FencingToken token) {
        if (token == null) return false;
        return token.isAuthoritative(currentEpoch.get());
    }

    @Override
    public synchronized Leadership tryAcquire(CoordinatorId candidate, Duration leaseDuration) {
        Objects.requireNonNull(candidate, "Candidate CoordinatorId cannot be null");
        members.add(candidate);

        DefaultLeadership existing = currentLeadership.get();
        if (existing == null || !existing.isValid() || existing.coordinator().equals(candidate)) {
            CoordinationEpoch nextEpoch = currentEpoch.updateAndGet(CoordinationEpoch::next);
            DefaultLeadership newLeadership = DefaultLeadership.of(candidate, nextEpoch, leaseDuration);
            currentLeadership.set(newLeadership);

            if (candidate.equals(identity.coordinatorId())) {
                state.set(LeadershipState.LEADER);
            } else {
                state.set(LeadershipState.FOLLOWER);
            }
            return newLeadership;
        }
        return existing;
    }

    @Override
    public Optional<Leadership> currentLeadership() {
        return leadership();
    }

    @Override
    public synchronized void abdicate(CoordinatorId leader) {
        DefaultLeadership existing = currentLeadership.get();
        if (existing != null && existing.coordinator().equals(leader)) {
            currentLeadership.set(null);
            if (leader.equals(identity.coordinatorId())) {
                state.set(LeadershipState.FOLLOWER);
            }
        }
    }
}
