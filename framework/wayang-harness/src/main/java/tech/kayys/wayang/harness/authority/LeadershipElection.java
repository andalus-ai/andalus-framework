package tech.kayys.wayang.harness.authority;

import java.time.Duration;
import java.util.Optional;

/**
 * Strategy interface for leader election and lease management.
 */
public interface LeadershipElection {

    Leadership tryAcquire(CoordinatorId candidate, Duration leaseDuration);

    Optional<Leadership> currentLeadership();

    void abdicate(CoordinatorId leader);
}
