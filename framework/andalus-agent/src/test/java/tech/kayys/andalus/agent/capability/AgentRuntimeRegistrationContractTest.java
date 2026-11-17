package tech.kayys.andalus.agent.capability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class AgentRuntimeRegistrationContractTest {

    @Test
    void requestNormalizesIdentityAndRequiresPositiveLeaseDuration() {
        var request = new AgentRuntimeRegistrationRequest(
                " tenant-a ", " researcher ", " pod-1 ", " node-1 ", Duration.ofSeconds(30));

        assertEquals("tenant-a", request.tenantId());
        assertEquals("researcher", request.agentId());
        assertEquals("pod-1", request.instanceId());
        assertEquals("node-1", request.ownerId());
        assertThrows(IllegalArgumentException.class, () -> new AgentRuntimeRegistrationRequest(
                "tenant-a", "researcher", "pod-1", "node-1", Duration.ZERO));
    }

    @Test
    void fencingTokenRequiresPositiveGenerationAndNonBlankIdentity() {
        assertThrows(IllegalArgumentException.class, () -> new AgentRuntimeLeaseToken(
                "tenant-a", "researcher", "pod-1", "node-1", 0));
        assertThrows(IllegalArgumentException.class, () -> new AgentRuntimeLeaseToken(
                "tenant-a", "researcher", " ", "node-1", 1));
    }
}
