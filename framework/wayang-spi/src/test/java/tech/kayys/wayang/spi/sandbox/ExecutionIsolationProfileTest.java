package tech.kayys.wayang.spi.sandbox;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionIsolationProfileTest {

    @Test
    void testProfileNone() {
        ExecutionIsolationProfile none = ExecutionIsolationProfile.none();

        assertEquals(IsolationLevel.NONE, none.level());
        assertEquals(SandboxType.NONE, none.preferredSandboxType());
        assertTrue(none.requiredFeatures().isEmpty());
        assertFalse(none.requires(IsolationFeature.PROCESS_ISOLATION));
    }

    @Test
    void testCustomIsolationProfile() {
        ExecutionIsolationProfile profile = new ExecutionIsolationProfile(
                IsolationLevel.STRONG,
                Set.of(IsolationFeature.PROCESS_ISOLATION, IsolationFeature.FILESYSTEM_ISOLATION, IsolationFeature.MEMORY_LIMITS),
                SandboxType.CONTAINER,
                SandboxLimits.unlimited(),
                SandboxFilesystem.empty(),
                SandboxNetwork.disabled(),
                Map.of("ENV_MODE", "isolated"),
                Map.of("pool", "default")
        );

        assertEquals(IsolationLevel.STRONG, profile.level());
        assertTrue(profile.requires(IsolationFeature.PROCESS_ISOLATION));
        assertTrue(profile.requires(IsolationFeature.FILESYSTEM_ISOLATION));
        assertFalse(profile.requires(IsolationFeature.NETWORK_ISOLATION));
        assertEquals("isolated", profile.environmentVariable("ENV_MODE").orElse(""));
    }

    @Test
    void testIsolationDecision() {
        IsolationDecision allowed = IsolationDecision.allowed(SandboxType.CONTAINER, "wayang.sandbox.container");
        assertInstanceOf(IsolationDecision.Allowed.class, allowed);
        IsolationDecision.Allowed a = (IsolationDecision.Allowed) allowed;
        assertEquals(SandboxType.CONTAINER, a.sandboxType());
        assertEquals("wayang.sandbox.container", a.providerId());

        IsolationDecision denied = IsolationDecision.denied("No provider supports SECCOMP");
        assertInstanceOf(IsolationDecision.Denied.class, denied);
        IsolationDecision.Denied d = (IsolationDecision.Denied) denied;
        assertEquals("No provider supports SECCOMP", d.reason());
    }
}
