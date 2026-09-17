package tech.kayys.wayang.harness.capability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.environment.CapabilityId;
import tech.kayys.wayang.spi.capability.Capability;
import tech.kayys.wayang.spi.capability.CapabilityDescriptor;
import tech.kayys.wayang.spi.capability.CapabilityInvocationResult;
import tech.kayys.wayang.spi.capability.CapabilityProviderRegistration;
import tech.kayys.wayang.spi.capability.CapabilityRegistry;
import tech.kayys.wayang.spi.capability.CapabilityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegistryBackedHarnessCapabilitiesTest {

    private TestCapabilityRegistry platformRegistry;
    private RegistryBackedHarnessCapabilities capabilities;

    @BeforeEach
    void setUp() {
        platformRegistry = new TestCapabilityRegistry();
        platformRegistry.register(new SimpleCapability("workspace.read", CapabilityType.of("tool", "workspace")));
        platformRegistry.register(new SimpleCapability("workspace.write", CapabilityType.of("tool", "workspace")));
        platformRegistry.register(new SimpleCapability("process.execute", CapabilityType.of("system", "process")));

        DefaultCapabilityScope scope = new DefaultCapabilityScope(
                Set.of("workspace.*"),
                Set.of("workspace.delete"),
                Set.of(),
                null
        );

        capabilities = new RegistryBackedHarnessCapabilities(platformRegistry, scope);
        capabilities.registerInvoker("workspace.read", ctx -> CapabilityInvocationResult.success("file-contents"));
    }

    @Test
    void filtersAvailableCapabilitiesThroughScope() {
        Set<CapabilityId> available = capabilities.available();
        assertTrue(available.contains(CapabilityId.of("workspace.read")));
        assertTrue(available.contains(CapabilityId.of("workspace.write")));
        assertFalse(available.contains(CapabilityId.of("process.execute"))); // in registry, but denied by scope
    }

    @Test
    void evaluatesRegisteredVsUnregistered() {
        CapabilityDecision registered = capabilities.evaluate(CapabilityRequest.of("workspace.read"));
        assertTrue(registered.isAllowed());

        CapabilityDecision unregistered = capabilities.evaluate(CapabilityRequest.of("unknown.capability"));
        assertFalse(unregistered.isAllowed());
        assertTrue(unregistered.reason().contains("not registered"));
    }

    @Test
    void executesInvocationThroughInvoker() throws Exception {
        CapabilityRequest req = CapabilityRequest.of("workspace.read", "read", Map.of("path", "/test.txt"));
        CapabilityInvocationResult result = capabilities.invoke(req, null);

        assertTrue(result.success());
        assertEquals("file-contents", result.output());
    }

    // Helper stubs
    private static class SimpleCapability implements Capability {
        private final String id;
        private final CapabilityType type;

        SimpleCapability(String id, CapabilityType type) {
            this.id = id;
            this.type = type;
        }

        @Override public String id() { return id; }
        @Override public CapabilityType type() { return type; }
        @Override public CapabilityDescriptor descriptor() { return null; }
    }

    private static class TestCapabilityRegistry implements CapabilityRegistry {
        private final List<Capability> list = new ArrayList<>();

        void register(Capability cap) { list.add(cap); }

        @Override public boolean contains(String capabilityId) {
            return list.stream().anyMatch(c -> c.id().equals(capabilityId));
        }
        @Override public Optional<Capability> find(String capabilityId) {
            return list.stream().filter(c -> c.id().equals(capabilityId)).findFirst();
        }
        @Override public List<Capability> findAll() { return List.copyOf(list); }
        @Override public List<Capability> findByType(CapabilityType type) { return List.of(); }
        @Override public Optional<String> providerOf(String capabilityId) { return Optional.of("test-provider"); }
        @Override public List<String> capabilitiesOf(String providerId) { return List.of(); }
        @Override public List<CapabilityProviderRegistration> providersOf(String capabilityId) { return List.of(); }
    }
}
