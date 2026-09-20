package tech.kayys.wayang.execution.core.resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.resource.binding.ResourceBinding;
import tech.kayys.wayang.execution.resource.binding.ResourceBindingState;
import tech.kayys.wayang.execution.resource.binding.ResourceBindings;
import tech.kayys.wayang.execution.resource.graph.ResourceEdge;
import tech.kayys.wayang.execution.resource.graph.ResourceRelation;
import tech.kayys.wayang.execution.resource.lifecycle.ResourceHealth;
import tech.kayys.wayang.execution.resource.lifecycle.ResourceLease;
import tech.kayys.wayang.execution.resource.model.ResourceId;
import tech.kayys.wayang.execution.resource.model.ResourceLifetime;
import tech.kayys.wayang.execution.resource.model.ResourceScope;
import tech.kayys.wayang.execution.resource.model.ResourceType;
import tech.kayys.wayang.execution.resource.model.VirtualResource;
import tech.kayys.wayang.execution.resource.policy.ResourcePolicyDecision;
import tech.kayys.wayang.execution.resource.provider.ResourceContext;
import tech.kayys.wayang.execution.resource.provider.ResourceProvider;
import tech.kayys.wayang.execution.resource.requirement.ComputeRequirement;
import tech.kayys.wayang.execution.resource.requirement.MemoryRequirement;
import tech.kayys.wayang.execution.resource.requirement.ModelRequirement;
import tech.kayys.wayang.execution.resource.requirement.NetworkRequirement;
import tech.kayys.wayang.execution.resource.requirement.SecretRequirement;
import tech.kayys.wayang.execution.resource.requirement.StorageRequirement;
import tech.kayys.wayang.execution.resource.requirement.ToolRequirement;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResourceVirtualizationTest {

    @Test
    @DisplayName("VirtualResource model and taxonomy correctness")
    void testVirtualResourceModel() {
        ResourceId id = ResourceId.of("execution/E42", "cpu");
        DefaultVirtualResource res = new DefaultVirtualResource(
                id,
                ResourceType.COMPUTE,
                ResourceScope.EXECUTION,
                ResourceLifetime.TASK,
                Map.of("quota", 4.0)
        );

        assertEquals(id, res.id());
        assertEquals(ResourceType.COMPUTE, res.type());
        assertEquals(ResourceScope.EXECUTION, res.scope());
        assertEquals(ResourceLifetime.TASK, res.lifetime());
        assertEquals(4.0, res.attributes().get("quota"));
    }

    @Test
    @DisplayName("ResourceRequirement types specify logical constraints correctly")
    void testRequirements() {
        ResourceId cpuId = ResourceId.of("cpu-1");
        ComputeRequirement compute = ComputeRequirement.of(cpuId, 2.0, 8.0);
        assertEquals(ResourceType.COMPUTE, compute.type());
        assertEquals(2.0, compute.minCpus());

        ResourceId memId = ResourceId.of("mem-1");
        MemoryRequirement mem = MemoryRequirement.megabytes(memId, 2048, 8192);
        assertEquals(ResourceType.MEMORY, mem.type());
        assertEquals(2048L * 1024L * 1024L, mem.minBytes());

        ResourceId storageId = ResourceId.of("storage-1");
        StorageRequirement storage = StorageRequirement.gigabytes(storageId, 10, true);
        assertEquals(ResourceType.STORAGE, storage.type());
        assertTrue(storage.persistent());

        ResourceId netId = ResourceId.of("net-1");
        NetworkRequirement net = NetworkRequirement.allowed(netId, List.of("pub.dev", "github.com"));
        assertEquals(ResourceType.NETWORK, net.type());
        assertTrue(net.egressAllowed());
        assertTrue(net.allowedHosts().contains("pub.dev"));

        ResourceId toolId = ResourceId.of("tool-flutter");
        ToolRequirement tool = ToolRequirement.of(toolId, "flutter");
        assertEquals(ResourceType.TOOL, tool.type());
        assertEquals("flutter", tool.toolName());

        ResourceId modelId = ResourceId.of("model-reasoner");
        ModelRequirement model = ModelRequirement.of(modelId, "reasoning");
        assertEquals(ResourceType.MODEL, model.type());
        assertEquals("reasoning", model.capability());

        ResourceId secId = ResourceId.of("sec-github");
        SecretRequirement sec = SecretRequirement.of(secId, "secret://tokens/github");
        assertEquals(ResourceType.SECRET, sec.type());
        assertEquals("secret://tokens/github", sec.secretUri());
    }

    @Test
    @DisplayName("ResourceGraph builds DAG and validates dependencies")
    void testResourceGraph() {
        ResourceId cpu = ResourceId.of("cpu");
        ResourceId ws = ResourceId.of("workspace");
        ResourceId dart = ResourceId.of("dart");
        ResourceId flutter = ResourceId.of("flutter");

        DefaultResourceGraph graph = new DefaultResourceGraph("graph-e42");
        graph.addResource(DefaultVirtualResource.of(cpu, ResourceType.COMPUTE))
                .addResource(DefaultVirtualResource.of(ws, ResourceType.STORAGE))
                .addResource(DefaultVirtualResource.of(dart, ResourceType.TOOL))
                .addResource(DefaultVirtualResource.of(flutter, ResourceType.TOOL));

        // Flutter REQUIRES Dart
        graph.addEdge(ResourceEdge.of(flutter, dart, ResourceRelation.REQUIRES));
        // Flutter MOUNTED_ON Workspace
        graph.addEdge(ResourceEdge.of(flutter, ws, ResourceRelation.MOUNTED_ON));

        assertEquals(4, graph.resources().size());
        assertEquals(2, graph.edges().size());

        DefaultResourceGraphValidator validator = new DefaultResourceGraphValidator();
        assertTrue(validator.validate(graph));

        var flutterDeps = graph.dependenciesOf(flutter);
        assertEquals(2, flutterDeps.size());
    }

    @Test
    @DisplayName("ResourceGraphValidator rejects missing endpoints and cycles")
    void testResourceGraphValidatorFailures() {
        DefaultResourceGraphValidator validator = new DefaultResourceGraphValidator();

        // 1. Missing target
        ResourceId a = ResourceId.of("a");
        DefaultResourceGraph graph1 = new DefaultResourceGraph("g1");
        graph1.addResource(DefaultVirtualResource.of(a, ResourceType.COMPUTE));
        graph1.addEdge(ResourceEdge.of(a, ResourceId.of("nonexistent"), ResourceRelation.REQUIRES));
        assertFalse(validator.validate(graph1));

        // 2. Self-loop
        DefaultResourceGraph graph2 = new DefaultResourceGraph("g2");
        graph2.addResource(DefaultVirtualResource.of(a, ResourceType.COMPUTE));
        graph2.addEdge(ResourceEdge.of(a, a, ResourceRelation.REQUIRES));
        assertFalse(validator.validate(graph2));

        // 3. Cycle: a -> b -> a
        ResourceId b = ResourceId.of("b");
        DefaultResourceGraph graph3 = new DefaultResourceGraph("g3");
        graph3.addResource(DefaultVirtualResource.of(a, ResourceType.COMPUTE));
        graph3.addResource(DefaultVirtualResource.of(b, ResourceType.COMPUTE));
        graph3.addEdge(ResourceEdge.of(a, b, ResourceRelation.REQUIRES));
        graph3.addEdge(ResourceEdge.of(b, a, ResourceRelation.REQUIRES));
        assertFalse(validator.validate(graph3));
    }

    @Test
    @DisplayName("DefaultResourceProvisioner provisions bindings and releases resources")
    void testResourceProvisioner() {
        DefaultResourceProvisioner provisioner = new DefaultResourceProvisioner();

        // Custom provider for tools
        provisioner.registerProvider(new ResourceProvider() {
            @Override
            public ResourceType type() {
                return ResourceType.TOOL;
            }

            @Override
            public boolean supports(VirtualResource resource) {
                return resource.type() == ResourceType.TOOL;
            }

            @Override
            public ResourceBinding provision(VirtualResource resource, ResourceContext context) {
                return ResourceBinding.active(
                        resource.id(),
                        "/opt/tools/" + resource.id().value(),
                        Map.of("hostPath", "/usr/bin/tool")
                );
            }

            @Override
            public void release(ResourceBinding binding, ResourceContext context) {
                // cleanup
            }
        });

        ResourceId toolId = ResourceId.of("flutter");
        ResourceId cpuId = ResourceId.of("cpu");

        VirtualResource r1 = DefaultVirtualResource.of(toolId, ResourceType.TOOL);
        VirtualResource r2 = DefaultVirtualResource.of(cpuId, ResourceType.COMPUTE);

        ResourceContext ctx = ResourceContext.of("exec-101", "local-node");
        ResourceBindings bindings = provisioner.provisionAll(List.of(r1, r2), ctx);

        assertEquals(2, bindings.size());
        assertTrue(bindings.contains(toolId));
        assertTrue(bindings.contains(cpuId));

        ResourceBinding b1 = bindings.get(toolId).orElseThrow();
        assertEquals(ResourceBindingState.ACTIVE, b1.state());
        assertEquals("/opt/tools/flutter", b1.physicalId());

        ResourceBinding b2 = bindings.get(cpuId).orElseThrow();
        assertEquals(ResourceBindingState.ACTIVE, b2.state());
        assertTrue(b2.physicalId().startsWith("mock-phys-"));

        assertDoesNotThrow(() -> provisioner.releaseAll(bindings, ctx));
    }

    @Test
    @DisplayName("ResourcePolicy and ResourceLease correctness")
    void testPolicyAndLease() {
        ResourcePolicyDecision allow = ResourcePolicyDecision.allow();
        assertTrue(allow.allowed());

        ResourcePolicyDecision deny = ResourcePolicyDecision.deny("Exceeded CPU quota");
        assertFalse(deny.allowed());
        assertEquals("Exceeded CPU quota", deny.reason());

        ResourceId id = ResourceId.of("gpu-1");
        Instant now = Instant.now();
        ResourceLease lease = new ResourceLease(id, "lease-1", now, now.plusSeconds(3600));
        assertFalse(lease.isExpired());
        assertEquals(ResourceHealth.HEALTHY, ResourceHealth.HEALTHY);
    }
}
