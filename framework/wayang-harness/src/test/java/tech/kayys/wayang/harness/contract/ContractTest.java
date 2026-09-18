package tech.kayys.wayang.harness.contract;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.contract.compatibility.CompatibilityContract;
import tech.kayys.wayang.harness.contract.lifecycle.DrainPolicy;
import tech.kayys.wayang.harness.contract.lifecycle.LifecycleContract;
import tech.kayys.wayang.harness.contract.lifecycle.LifecycleFeature;
import tech.kayys.wayang.harness.contract.model.*;
import tech.kayys.wayang.harness.contract.protocol.*;
import tech.kayys.wayang.harness.contract.registry.AgentContractRegistry;
import tech.kayys.wayang.harness.contract.schema.SchemaRef;
import tech.kayys.wayang.harness.contract.version.ContractVersion;
import tech.kayys.wayang.harness.protocol.AgentRef;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ContractTest {

    @Test
    void testContractVersionCompatibility() {
        ContractVersion v1_0 = ContractVersion.of(1, 0);
        ContractVersion v1_1 = ContractVersion.of(1, 1);
        ContractVersion v2_0 = ContractVersion.of(2, 0);

        assertTrue(v1_1.isCompatibleWith(v1_0));
        assertFalse(v1_0.isCompatibleWith(v1_1)); // older minor cannot satisfy newer minor
        assertFalse(v1_0.isCompatibleWith(v2_0)); // different major
        assertFalse(v2_0.isCompatibleWith(v1_0));
    }

    @Test
    void testAgentContractRegistry() {
        AgentContractRegistry registry = AgentContractRegistry.inMemory();

        DefaultAgentContract contract = new DefaultAgentContract(
                AgentRef.of("agent-flutter"),
                AgentType.of("flutter-reviewer"),
                ContractVersion.of(1, 2),
                AgentCapabilities.of(Set.of("filesystem.read"), Set.of()),
                new InputContract(Set.of("review-task"), SchemaRef.of("review-schema", "1.0"), Set.of("repo")),
                new OutputContract(SchemaRef.of("review-result", "1.0"), Set.of("diff"), Set.of("artifact-diff")),
                new ContextContract(Set.of("git-tree"), Set.of()),
                new LifecycleContract(Set.of(LifecycleFeature.DRAIN), DrainPolicy.defaultPolicy()),
                new ProtocolContract(
                        ContractVersion.of(1, 2),
                        Set.of(TransportType.IN_PROCESS, TransportType.GRPC),
                        Set.of(ProtocolFeature.STREAMING, ProtocolFeature.PROGRESS_REPORTING)
                ),
                CompatibilityContract.semanticMajorMatch(ContractVersion.of(1, 2))
        );

        registry.register(contract);

        assertTrue(registry.resolve(AgentRef.of("agent-flutter")).isPresent());
        assertEquals(1, registry.findCompatible(AgentType.of("flutter-reviewer"), ContractVersion.of(1, 0)).size());
        assertEquals(0, registry.findCompatible(AgentType.of("flutter-reviewer"), ContractVersion.of(2, 0)).size());
    }

    @Test
    void testHandshakeSessionNegotiation() {
        ProtocolContract harnessProtocol = new ProtocolContract(
                ContractVersion.of(1, 0),
                Set.of(TransportType.IN_PROCESS, TransportType.GRPC),
                Set.of(ProtocolFeature.STREAMING, ProtocolFeature.PROGRESS_REPORTING, ProtocolFeature.CANCELLATION)
        );

        AgentContract offer = new DefaultAgentContract(
                AgentRef.of("agent-client"),
                AgentType.of("client"),
                ContractVersion.of(1, 1),
                AgentCapabilities.empty(),
                new InputContract(Set.of("task"), SchemaRef.of("in", "1"), Set.of()),
                new OutputContract(SchemaRef.of("out", "1"), Set.of(), Set.of()),
                ContextContract.empty(),
                LifecycleContract.standard(),
                new ProtocolContract(
                        ContractVersion.of(1, 0),
                        Set.of(TransportType.GRPC),
                        Set.of(ProtocolFeature.STREAMING)
                ),
                CompatibilityContract.semanticMajorMatch(ContractVersion.of(1, 1))
        );

        HandshakeSession session = new HandshakeSession();
        assertEquals(HandshakeStep.WAYANG_HELLO, session.step());

        session.onAgentHello(offer.identity());
        assertEquals(HandshakeStep.CONTRACT_OFFER, session.step());

        HandshakeStep result = session.onContractOffer(offer, harnessProtocol);
        assertEquals(HandshakeStep.READY, result);
        assertEquals(HandshakeStep.READY, session.step());
        assertTrue(session.features().contains(ProtocolFeature.STREAMING));
        assertFalse(session.features().contains(ProtocolFeature.CANCELLATION));
    }
}
