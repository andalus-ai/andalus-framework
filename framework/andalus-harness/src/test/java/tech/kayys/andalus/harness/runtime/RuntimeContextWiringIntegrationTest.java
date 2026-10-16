package tech.kayys.andalus.harness.runtime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.api.HarnessExecution;
import tech.kayys.andalus.harness.api.HarnessRequest;
import tech.kayys.andalus.harness.api.HarnessResult;
import tech.kayys.andalus.harness.capability.DefaultCapabilityScope;
import tech.kayys.andalus.harness.capability.RegistryBackedHarnessCapabilities;
import tech.kayys.andalus.harness.context.ContextContribution;
import tech.kayys.andalus.harness.context.ContextKind;
import tech.kayys.andalus.harness.context.ContextRequest;
import tech.kayys.andalus.harness.context.DefaultContextAssembler;
import tech.kayys.andalus.harness.context.KnowledgeContextProvider;
import tech.kayys.andalus.harness.context.MemoryContextProvider;
import tech.kayys.andalus.harness.lifecycle.HarnessExecutionStatus;
import tech.kayys.andalus.harness.memory.DefaultMemoryRuntime;
import tech.kayys.andalus.harness.memory.InMemoryMemoryStore;
import tech.kayys.andalus.harness.memory.MemoryContext;
import tech.kayys.andalus.harness.memory.MemoryEntry;
import tech.kayys.andalus.harness.memory.MemoryRuntime;
import tech.kayys.andalus.harness.memory.MemoryScope;
import tech.kayys.andalus.knowledge.KnowledgeContext;
import tech.kayys.andalus.knowledge.KnowledgeEvidence;
import tech.kayys.andalus.knowledge.KnowledgeItem;
import tech.kayys.andalus.knowledge.KnowledgeResolver;
import tech.kayys.andalus.knowledge.KnowledgeResult;
import tech.kayys.andalus.spi.capability.Capability;
import tech.kayys.andalus.spi.capability.CapabilityDescriptor;
import tech.kayys.andalus.spi.capability.CapabilityProviderRegistration;
import tech.kayys.andalus.spi.capability.CapabilityRegistry;
import tech.kayys.andalus.spi.capability.CapabilityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeContextWiringIntegrationTest {

    private MemoryRuntime memoryRuntime;
    private MemoryContext memoryContext;
    private KnowledgeResolver knowledgeResolver;
    private KnowledgeContext knowledgeContext;

    @BeforeEach
    void setUp() {
        // Memory setup
        var syncStore = new InMemoryMemoryStore();
        memoryRuntime = new DefaultMemoryRuntime(syncStore);
        memoryContext = MemoryContext.forSession("session-1", "agent-1");

        // Preload memory
        memoryRuntime.remember(MemoryEntry.fact("User prefers dark mode and concise summaries", MemoryScope.SESSION), memoryContext)
                .toCompletableFuture()
                .join();

        // Knowledge setup
        knowledgeContext = KnowledgeContext.empty();
        knowledgeResolver = (query, ctx) -> {
            KnowledgeItem item = new KnowledgeItem(
                    "rule-1",
                    "kb-core",
                    "policy",
                    "Security Guidelines",
                    "All API tokens must be rotated every 90 days",
                    java.util.Map.of(),
                    null,
                    null,
                    null
            );
            KnowledgeEvidence evidence = KnowledgeEvidence.of(item, 0.95, "vector_search");
            return CompletableFuture.completedFuture(KnowledgeResult.of(query, List.of(evidence)));
        };
    }

    @Test
    void testMemoryContextProviderProvidesItems() {
        MemoryContextProvider provider = new MemoryContextProvider(memoryRuntime, memoryContext);
        ContextRequest req = ContextRequest.of("dark mode", 1000);

        ContextContribution contribution = provider.provide(req);

        assertThat(contribution.items()).isNotEmpty();
        assertThat(contribution.items().get(0).kind()).isEqualTo(ContextKind.MEMORY);
        assertThat(contribution.items().get(0).content()).contains("dark mode");
    }

    @Test
    void testKnowledgeContextProviderProvidesItems() {
        KnowledgeContextProvider provider = new KnowledgeContextProvider(knowledgeResolver, knowledgeContext);
        ContextRequest req = ContextRequest.of("API tokens", 1000);

        ContextContribution contribution = provider.provide(req);

        assertThat(contribution.items()).isNotEmpty();
        assertThat(contribution.items().get(0).kind()).isEqualTo(ContextKind.KNOWLEDGE);
        assertThat(contribution.items().get(0).content()).contains("90 days");
    }

    @Test
    void testDefaultHarnessWithContextAssemblerIntegratesBoth() throws Exception {
        DefaultContextAssembler assembler = new DefaultContextAssembler(List.of(
                new MemoryContextProvider(memoryRuntime, memoryContext),
                new KnowledgeContextProvider(knowledgeResolver, knowledgeContext)
        ));

        TestCapabilityRegistry registry = new TestCapabilityRegistry();
        registry.register(new SimpleCapability("workspace.read", CapabilityType.of("tool", "workspace")));
        DefaultCapabilityScope scope = new DefaultCapabilityScope(Set.of("workspace.*"), Set.of(), Set.of(), null);
        RegistryBackedHarnessCapabilities capabilities = new RegistryBackedHarnessCapabilities(registry, scope);

        DefaultHarness harness = new DefaultHarness(
                capabilities,
                new tech.kayys.andalus.harness.environment.DefaultHarnessResources(),
                tech.kayys.andalus.harness.resource.DefaultResourceScope.allowAll(),
                java.util.concurrent.ForkJoinPool.commonPool(),
                assembler
        );

        HarnessRequest request = HarnessRequest.of("agent-1", "dark mode");
        HarnessExecution execution = harness.start(request);
        HarnessResult result = execution.completion().get(5, TimeUnit.SECONDS);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.status()).isEqualTo(HarnessExecutionStatus.COMPLETED);
        String output = result.output().toString();
        // Verifies context assembly was triggered and items were counted
        assertThat(output).contains("contextItems=2");
    }

    // Stubs
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
