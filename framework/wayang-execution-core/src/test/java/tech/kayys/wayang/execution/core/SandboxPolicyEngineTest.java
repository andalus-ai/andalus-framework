package tech.kayys.wayang.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.core.policy.DefaultSandboxPolicyEngine;
import tech.kayys.wayang.execution.core.profile.DefaultSandboxProfileRegistry;
import tech.kayys.wayang.execution.policy.PolicyDecision;
import tech.kayys.wayang.execution.policy.SandboxOperation;
import tech.kayys.wayang.execution.sandbox.SandboxContext;
import tech.kayys.wayang.execution.sandbox.SandboxProfile;
import tech.kayys.wayang.execution.sandbox.SandboxSpec;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SandboxPolicyEngineTest {

    @Test
    void testCodingProfilePolicyEnforcement() {
        DefaultSandboxProfileRegistry registry = new DefaultSandboxProfileRegistry();
        SandboxContext context = SandboxContext.of("tenant-1", "exec-1", "agent-coder");
        SandboxSpec codingSpec = registry.resolve(SandboxProfile.PROFILE_CODING, context);
        DefaultSandboxPolicyEngine engine = new DefaultSandboxPolicyEngine(codingSpec);

        // Allowed process
        PolicyDecision gitDecision = engine.evaluate(SandboxOperation.of("PROCESS", "git"), context);
        assertTrue(gitDecision.permitted());

        // Denied process
        PolicyDecision curlDecision = engine.evaluate(SandboxOperation.of("PROCESS", "curl"), context);
        assertFalse(curlDecision.permitted());

        // Allowed network destination
        PolicyDecision githubDecision = engine.evaluate(SandboxOperation.of("NETWORK", "github.com"), context);
        assertTrue(githubDecision.permitted());

        // Denied network destination
        PolicyDecision evilDecision = engine.evaluate(SandboxOperation.of("NETWORK", "malicious-site.com"), context);
        assertFalse(evilDecision.permitted());

        // Read-write filesystem
        PolicyDecision fsWrite = engine.evaluate(new SandboxOperation("FILESYSTEM", "src/Main.java", Map.of("op", "WRITE")), context);
        assertTrue(fsWrite.permitted());
    }

    @Test
    void testMinimalProfilePolicyEnforcement() {
        DefaultSandboxProfileRegistry registry = new DefaultSandboxProfileRegistry();
        SandboxContext context = SandboxContext.defaultContext();
        SandboxSpec minimalSpec = registry.resolve(SandboxProfile.PROFILE_MINIMAL, context);
        DefaultSandboxPolicyEngine engine = new DefaultSandboxPolicyEngine(minimalSpec);

        // Process denied
        PolicyDecision procDecision = engine.evaluate(SandboxOperation.of("PROCESS", "bash"), context);
        assertFalse(procDecision.permitted());

        // Network denied
        PolicyDecision netDecision = engine.evaluate(SandboxOperation.of("NETWORK", "localhost"), context);
        assertFalse(netDecision.permitted());

        // Filesystem read allowed, write denied
        PolicyDecision fsRead = engine.evaluate(new SandboxOperation("FILESYSTEM", "readme.txt", Map.of("op", "READ")), context);
        assertTrue(fsRead.permitted());

        PolicyDecision fsWrite = engine.evaluate(new SandboxOperation("FILESYSTEM", "readme.txt", Map.of("op", "WRITE")), context);
        assertFalse(fsWrite.permitted());
    }
}
