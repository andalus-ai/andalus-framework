package tech.kayys.wayang.harness.authority;

import java.util.Optional;

/**
 * Coordination SPI managing high-availability leadership, membership, and authority validation.
 */
public interface CoordinationService {

    CoordinationIdentity identity();

    LeadershipState state();

    Optional<Leadership> leadership();

    LeadershipElection election();

    MembershipView membership();

    boolean validateAuthority(FencingToken token);
}
