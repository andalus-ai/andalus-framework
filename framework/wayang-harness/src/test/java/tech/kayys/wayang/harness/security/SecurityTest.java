package tech.kayys.wayang.harness.security;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.security.authorization.AuthorizationDecision;
import tech.kayys.wayang.harness.security.authorization.AuthorizationRequest;
import tech.kayys.wayang.harness.security.authorization.DefaultAuthorizationEngine;
import tech.kayys.wayang.harness.security.capability.CapabilityConstraints;
import tech.kayys.wayang.harness.security.capability.CapabilityLease;
import tech.kayys.wayang.harness.security.capability.CapabilityLeaseId;
import tech.kayys.wayang.harness.security.capability.DefaultCapabilityLease;
import tech.kayys.wayang.harness.security.identity.Principal;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SecurityTest {

    @Test
    void testAuthorizationEngineEvaluation() {
        DefaultAuthorizationEngine engine = new DefaultAuthorizationEngine(
                Set.of("filesystem.read", "model.chat"),
                Set.of("filesystem.write", "secret.raw")
        );

        Principal agent = new Principal.AgentPrincipal("agent-101", Set.of("developer"));
        ExecutionId execId = ExecutionId.of("exec-sec-1");

        AuthorizationDecision d1 = engine.authorize(new AuthorizationRequest(agent, execId, "filesystem.read", CapabilityConstraints.unconstrained()));
        assertTrue(d1 instanceof AuthorizationDecision.Allowed);

        AuthorizationDecision d2 = engine.authorize(new AuthorizationRequest(agent, execId, "filesystem.write", CapabilityConstraints.unconstrained()));
        assertTrue(d2 instanceof AuthorizationDecision.Denied);

        AuthorizationDecision d3 = engine.authorize(new AuthorizationRequest(agent, execId, "network.connect", CapabilityConstraints.unconstrained()));
        assertTrue(d3 instanceof AuthorizationDecision.Denied);
    }

    @Test
    void testCapabilityLeaseRevocation() {
        Principal agent = new Principal.AgentPrincipal("agent-102", Set.of("developer"));
        ExecutionId execId = ExecutionId.of("exec-sec-2");

        CapabilityLease lease = new DefaultCapabilityLease(
                CapabilityLeaseId.generate(),
                "filesystem.read",
                agent,
                execId,
                CapabilityConstraints.of("path", "/workspace/**"),
                Instant.now().plusSeconds(300)
        );

        assertTrue(lease.isValid());
        assertEquals("filesystem.read", lease.capabilityId());

        lease.revoke("Admin intervention");
        assertFalse(lease.isValid());
    }
}
