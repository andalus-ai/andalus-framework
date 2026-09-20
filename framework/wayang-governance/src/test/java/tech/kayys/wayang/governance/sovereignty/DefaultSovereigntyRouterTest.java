package tech.kayys.wayang.governance.sovereignty;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultSovereigntyRouterTest {

    private DefaultSovereigntyRouter router;

    @BeforeEach
    void setUp() {
        router = new DefaultSovereigntyRouter();
    }

    @Test
    void testSameJurisdictionAllowed() {
        SovereigntyPolicy strictPolicy = SovereigntyPolicy.strict("pol-eu", "EU");

        boolean allowed = router.canDispatch("workload-1", strictPolicy, "EU");
        assertThat(allowed).isTrue();
    }

    @Test
    void testStrictPolicyRejectsCrossBorder() {
        SovereigntyPolicy strictPolicy = SovereigntyPolicy.strict("pol-eu", "EU");

        boolean allowed = router.canDispatch("workload-1", strictPolicy, "US");
        assertThat(allowed).isFalse();
    }

    @Test
    void testPermissivePolicyAllowsEnclave() {
        SovereigntyPolicy permissive = SovereigntyPolicy.permissive("pol-eu", "EU", Set.of("CH", "NO"));

        assertThat(router.canDispatch("workload-1", permissive, "CH")).isTrue();
        assertThat(router.canDispatch("workload-1", permissive, "NO")).isTrue();
        assertThat(router.canDispatch("workload-1", permissive, "US")).isFalse();
    }
}
