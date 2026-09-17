package tech.kayys.wayang.harness.runtime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.api.Harness;
import tech.kayys.wayang.harness.api.HarnessExecution;
import tech.kayys.wayang.harness.api.HarnessRequest;
import tech.kayys.wayang.harness.api.HarnessResult;
import tech.kayys.wayang.harness.capability.DefaultCapabilityScope;
import tech.kayys.wayang.harness.capability.RegistryBackedHarnessCapabilities;
import tech.kayys.wayang.harness.environment.CapabilityId;
import tech.kayys.wayang.harness.lifecycle.HarnessExecutionStatus;
import tech.kayys.wayang.spi.capability.Capability;
import tech.kayys.wayang.spi.capability.CapabilityDescriptor;
import tech.kayys.wayang.spi.capability.CapabilityProviderRegistration;
import tech.kayys.wayang.spi.capability.CapabilityRegistry;
import tech.kayys.wayang.spi.capability.CapabilityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DefaultHarnessTest {

    private Harness harness;

    @BeforeEach
    void setUp() {
        TestCapabilityRegistry registry = new TestCapabilityRegistry();
        registry.register(new SimpleCapability("workspace.read", CapabilityType.of("tool", "workspace")));
        registry.register(new SimpleCapability("process.execute", CapabilityType.of("system", "process")));

        DefaultCapabilityScope scope = new DefaultCapabilityScope(
                Set.of("workspace.*"),
                Set.of(),
                Set.of(),
                null
        );

        RegistryBackedHarnessCapabilities capabilities = new RegistryBackedHarnessCapabilities(registry, scope);
        harness = new DefaultHarness(capabilities);
    }

    @Test
    void successfulExecution() throws Exception {
        HarnessRequest request = HarnessRequest.of(
                "agent-aljabr",
                "Analyze codebase",
                Set.of(CapabilityId.of("workspace.read"))
        );

        HarnessExecution execution = harness.start(request);
        assertNotNull(execution.id());

        CompletableFuture<HarnessResult> future = execution.completion();
        HarnessResult result = future.get(5, TimeUnit.SECONDS);

        assertTrue(result.isSuccess());
        assertEquals(HarnessExecutionStatus.COMPLETED, result.status());
        assertTrue(result.output().toString().contains("Agent [agent-aljabr] executed prompt: Analyze codebase"));
    }

    @Test
    void admissionRejectionOnMissingOrUnauthorizedCapability() throws Exception {
        HarnessRequest request = HarnessRequest.of(
                "agent-unauthorized",
                "Execute root command",
                Set.of(CapabilityId.of("process.execute")) // Not permitted in scope
        );

        HarnessExecution execution = harness.start(request);
        HarnessResult result = execution.completion().get(5, TimeUnit.SECONDS);

        assertFalse(result.isSuccess());
        assertEquals(HarnessExecutionStatus.FAILED, result.status());
        assertTrue(result.error().contains("Admission denied"));
    }

    @Test
    void executeConvenienceMethod() throws Exception {
        HarnessRequest request = HarnessRequest.of(
                "agent-simple",
                "Simple query"
        );

        HarnessResult result = harness.execute(request).get(5, TimeUnit.SECONDS);
        assertTrue(result.isSuccess());
        assertEquals(HarnessExecutionStatus.COMPLETED, result.status());
    }

    // Stub
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
        @Override public Optional<String> providerOf(String capabilityId) { return Optional.of("test"); }
        @Override public List<String> capabilitiesOf(String providerId) { return List.of(); }
        @Override public List<CapabilityProviderRegistration> providersOf(String capabilityId) { return List.of(); }
    }
}
