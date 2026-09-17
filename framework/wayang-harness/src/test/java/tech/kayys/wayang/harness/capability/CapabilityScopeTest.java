package tech.kayys.wayang.harness.capability;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CapabilityScopeTest {

    @Test
    void wildcardScopeAllowsAllNonDenied() {
        DefaultCapabilityScope scope = new DefaultCapabilityScope(
                Set.of("*"),
                Set.of("network.raw_socket"),
                Set.of(),
                null
        );

        assertTrue(scope.allows("workspace.read"));
        assertTrue(scope.allows("process.execute"));
        assertFalse(scope.allows("network.raw_socket"));

        assertEquals(CapabilityDecisionType.ALLOW, scope.evaluate(CapabilityRequest.of("workspace.read")).type());
        assertEquals(CapabilityDecisionType.DENY, scope.evaluate(CapabilityRequest.of("network.raw_socket")).type());
    }

    @Test
    void prefixWildcardMatching() {
        DefaultCapabilityScope scope = new DefaultCapabilityScope(
                Set.of("workspace.*", "git.*"),
                Set.of("workspace.delete"),
                Set.of(),
                null
        );

        assertTrue(scope.allows("workspace.read"));
        assertTrue(scope.allows("workspace.write"));
        assertTrue(scope.allows("git.commit"));

        assertFalse(scope.allows("workspace.delete"));
        assertFalse(scope.allows("process.execute"));
    }

    @Test
    void requiresApprovalEvaluation() {
        DefaultCapabilityScope scope = new DefaultCapabilityScope(
                Set.of("*"),
                Set.of(),
                Set.of("process.execute"),
                req -> "rm -rf".equals(req.arguments().get("command"))
        );

        CapabilityDecision processDecision = scope.evaluate(CapabilityRequest.of("process.execute"));
        assertEquals(CapabilityDecisionType.REQUIRE_APPROVAL, processDecision.type());

        CapabilityDecision dangerousReq = scope.evaluate(CapabilityRequest.of("shell.execute", "run", Map.of("command", "rm -rf")));
        assertEquals(CapabilityDecisionType.REQUIRE_APPROVAL, dangerousReq.type());

        CapabilityDecision safeReq = scope.evaluate(CapabilityRequest.of("workspace.read", "read", Map.of("path", "/tmp/test")));
        assertEquals(CapabilityDecisionType.ALLOW, safeReq.type());
    }
}
