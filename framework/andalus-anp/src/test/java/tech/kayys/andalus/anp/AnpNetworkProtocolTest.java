package tech.kayys.andalus.anp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.anp.auth.AnpAuthenticator;
import tech.kayys.andalus.anp.client.AnpClient;
import tech.kayys.andalus.anp.client.AnpNetworkClient;
import tech.kayys.andalus.anp.client.AnpNetworkProtocol;
import tech.kayys.andalus.anp.config.AnpProtocolConfig;
import tech.kayys.andalus.anp.identity.AnpIdentityKeyPair;
import tech.kayys.andalus.anp.identity.InMemoryAnpKeyStore;
import tech.kayys.andalus.anp.meta.AnpMetaProtocolNegotiator;
import tech.kayys.andalus.communication.api.AgentRef;
import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.capability.CapabilityId;
import tech.kayys.andalus.communication.message.MessagePayload;
import tech.kayys.andalus.network.AgentNetworkRequest;
import tech.kayys.andalus.network.AgentNetworkResponse;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AnpNetworkProtocolTest {

    private AnpNetworkProtocol protocol;
    private AnpClient anpClient;

    @BeforeEach
    void setUp() {
        InMemoryAnpKeyStore keyStore = new InMemoryAnpKeyStore();
        AnpAuthenticator auth = new AnpAuthenticator();
        AnpMetaProtocolNegotiator meta = new AnpMetaProtocolNegotiator();

        byte[] secret = "test-secret-key-32bytes-long!!".getBytes(StandardCharsets.UTF_8);
        AnpIdentityKeyPair key = new AnpIdentityKeyPair("key-1", "hmac-sha256", secret, secret);
        keyStore.storeKey("sender-agent", key);

        anpClient = new AnpClient(keyStore, auth, meta);
        AnpNetworkClient client = new AnpNetworkClient(anpClient);
        AnpProtocolConfig config = AnpProtocolConfig.enabled("test.andalus.local");

        protocol = new AnpNetworkProtocol(client, config);
    }

    @Test
    void protocolMetadataIsCorrect() {
        assertEquals("anp", protocol.id());
        assertEquals("1.1", protocol.version());
        assertTrue(protocol.isEnabled());
        assertNotNull(protocol.client());
    }

    @Test
    void invokesRemoteAnpAgentSuccessfully() throws Exception {
        AgentRef target = AgentRef.remote(
                "did:wba:remote.agent.org:analyst",
                "Remote Analyst",
                URI.create("anp://did:wba:remote.agent.org:analyst")
        );

        AgentRequest agentRequest = AgentRequest.of(
                target,
                CapabilityId.of("analysis.report"),
                MessagePayload.text("Please analyze the repo")
        );

        SecurityContextSnapshot security = new SecurityContextSnapshot(
                Principal.agent("sender-agent", "Sender Agent"),
                TenantContext.empty(),
                Optional.empty(),
                Map.of()
        );

        AgentNetworkRequest netRequest = AgentNetworkRequest.of(agentRequest, security);

        AgentNetworkResponse response = protocol.client().invoke(netRequest).toCompletableFuture().get();

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("anp/1.1", response.metadata().get("protocol"));
        assertEquals("did:wba:remote.agent.org:analyst", response.metadata().get("targetDid"));
    }
}
