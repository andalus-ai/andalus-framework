package tech.kayys.andalus.execution.core.fabric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.conformance.CapabilityDescriptor;
import tech.kayys.andalus.execution.conformance.CapabilityId;
import tech.kayys.andalus.execution.conformance.CapabilityLevel;
import tech.kayys.andalus.execution.conformance.CapabilityRequirement;
import tech.kayys.andalus.execution.conformance.CapabilityVersion;
import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.conformance.ProfileId;
import tech.kayys.andalus.execution.fabric.FabricDiscovery;
import tech.kayys.andalus.execution.fabric.FabricSnapshot;
import tech.kayys.andalus.execution.fabric.NodeDescriptor;
import tech.kayys.andalus.execution.fabric.NodeIdentity;
import tech.kayys.andalus.execution.fabric.NodeRegistry;
import tech.kayys.andalus.execution.fabric.NodeStatus;
import tech.kayys.andalus.execution.fabric.PlacementEngine;
import tech.kayys.andalus.execution.fabric.PlacementPlan;
import tech.kayys.andalus.execution.fabric.TopologyDescriptor;
import tech.kayys.andalus.execution.fabric.AndalusNode;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.portability.RuntimeIdentity;
import tech.kayys.andalus.execution.resource.requirement.ResourceRequirement;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionFabricTest {

    private NodeRegistry registry;
    private FabricDiscovery discovery;
    private PlacementEngine placementEngine;

    @BeforeEach
    void setUp() {
        registry = new DefaultNodeRegistry();
        discovery = new DefaultFabricDiscovery(registry, "fabric-test");
        placementEngine = new DefaultPlacementEngine();
    }

    @Test
    void testNodeRegistryAndHeartbeat() {
        NodeIdentity id = NodeIdentity.of("cluster-1", "node-1", 1L);
        ConformanceProfile profile = ConformanceProfile.of(ProfileId.of("prof-1"), Set.of(), Set.of());
        NodeDescriptor desc = new NodeDescriptor(id, RuntimeIdentity.of("local", "jvm", "21"), profile, TopologyDescriptor.local());
        TestAndalusNode node = new TestAndalusNode(id, desc, NodeStatus.healthy(0.2), profile);

        registry.register(node);
        assertTrue(registry.get(id).isPresent());
        assertEquals(1, registry.allNodes().size());

        registry.heartbeat(id, 0.45);
        assertEquals(0.45, registry.get(id).get().status().loadFactor(), 0.001);

        FabricSnapshot snapshot = discovery.captureSnapshot();
        assertEquals(1, snapshot.nodes().size());
        assertEquals(1, snapshot.availableNodes().size());
    }

    @Test
    void testPlacementEngineSelectsOptimalNode() {
        CapabilityId gpuCap = CapabilityId.of("execution", "hardware.gpu");
        CapabilityDescriptor gpuDesc = CapabilityDescriptor.of(gpuCap, CapabilityVersion.of(1, 0, 0), CapabilityLevel.ADVANCED);

        // Node 1 has GPU and load 0.3
        NodeIdentity id1 = NodeIdentity.of("cluster-1", "gpu-node-1", 1L);
        ConformanceProfile prof1 = ConformanceProfile.of(ProfileId.of("prof-gpu"), Set.of(gpuDesc), Set.of());
        NodeDescriptor desc1 = new NodeDescriptor(id1, RuntimeIdentity.of("local", "jvm", "21"), prof1, TopologyDescriptor.local());
        TestAndalusNode node1 = new TestAndalusNode(id1, desc1, NodeStatus.healthy(0.3), prof1);

        // Node 2 has GPU and load 0.8 (higher load)
        NodeIdentity id2 = NodeIdentity.of("cluster-1", "gpu-node-2", 1L);
        TestAndalusNode node2 = new TestAndalusNode(id2, desc1, NodeStatus.healthy(0.8), prof1);

        registry.register(node1);
        registry.register(node2);

        FabricSnapshot snapshot = discovery.captureSnapshot();
        ExecutionIdentity execId = ExecutionIdentity.of("exec-gpu-task", "wf-1", "agent-1");
        CapabilityRequirement req = CapabilityRequirement.of(gpuCap, CapabilityLevel.STANDARD);

        Optional<PlacementPlan> plan = placementEngine.plan(execId, Set.of(req), Set.of(), snapshot);
        assertTrue(plan.isPresent());
        // Should choose node1 because it has lower load (0.3 vs 0.8)
        assertEquals(id1, plan.get().targetNode());
        assertTrue(plan.get().evidence().score() > 50.0);
    }

    private static class TestAndalusNode implements AndalusNode {
        private final NodeIdentity id;
        private final NodeDescriptor descriptor;
        private final NodeStatus status;
        private final ConformanceProfile profile;

        TestAndalusNode(NodeIdentity id, NodeDescriptor descriptor, NodeStatus status, ConformanceProfile profile) {
            this.id = id;
            this.descriptor = descriptor;
            this.status = status;
            this.profile = profile;
        }

        @Override public NodeIdentity identity() { return id; }
        @Override public NodeDescriptor descriptor() { return descriptor; }
        @Override public NodeStatus status() { return status; }
        @Override public ConformanceProfile profile() { return profile; }
        @Override public Set<ResourceRequirement> availableResources() { return Set.of(); }
    }
}
