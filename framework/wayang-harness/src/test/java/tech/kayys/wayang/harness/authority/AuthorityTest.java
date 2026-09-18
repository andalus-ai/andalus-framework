package tech.kayys.wayang.harness.authority;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AuthorityTest {

    @Test
    void testLeadershipElectionAndFencingToken() {
        CoordinatorId coord1 = CoordinatorId.of("coordinator-1");
        CoordinatorId coord2 = CoordinatorId.of("coordinator-2");

        CoordinationIdentity id1 = CoordinationIdentity.local(coord1);
        DefaultCoordinationService coordinationService = new DefaultCoordinationService(id1);

        // Initial leader should be none
        assertTrue(coordinationService.leadership().isEmpty());

        // Try acquire leadership by coord1 -> increments epoch to 2
        Leadership lead1 = coordinationService.tryAcquire(coord1, Duration.ofMinutes(1));
        assertTrue(lead1.isValid());
        assertEquals(coord1, lead1.coordinator());
        assertEquals(CoordinationEpoch.of(2), lead1.epoch());

        // Verify current leader
        Leadership current = coordinationService.leadership().orElseThrow();
        assertEquals(coord1, current.coordinator());

        // Token generated from active leader should be valid
        FencingToken token1 = DefaultFencingToken.of(100L, lead1.epoch());
        assertTrue(coordinationService.validateAuthority(token1));

        // Stale epoch token should fail validation
        FencingToken staleToken = DefaultFencingToken.of(100L, CoordinationEpoch.of(1));
        assertFalse(coordinationService.validateAuthority(staleToken));

        // Abdicate leadership
        coordinationService.abdicate(coord1);
        assertTrue(coordinationService.leadership().isEmpty());

        // Next acquire by coord2 increments epoch to 3
        Leadership lead2 = coordinationService.tryAcquire(coord2, Duration.ofMinutes(1));
        assertEquals(CoordinationEpoch.of(3), lead2.epoch());
        assertEquals(coord2, lead2.coordinator());
    }
}
