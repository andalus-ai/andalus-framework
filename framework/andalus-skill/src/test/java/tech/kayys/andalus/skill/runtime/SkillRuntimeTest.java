package tech.kayys.andalus.skill.runtime;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.descriptor.CapabilityDescriptor;
import tech.kayys.andalus.descriptor.ParameterDescriptor;
import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.resource.BaseResource;
import tech.kayys.andalus.resource.ResourceType;
import tech.kayys.andalus.skill.spi.Skill;
import tech.kayys.andalus.skill.spi.SkillContext;
import tech.kayys.andalus.skill.spi.SkillDescriptor;
import tech.kayys.andalus.skill.spi.SkillReference;
import tech.kayys.andalus.skill.spi.SkillRegistry;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkillRuntimeTest {

    static class TestDescriptor extends BaseResource implements SkillDescriptor {
        private final String category;

        TestDescriptor(ResourceId id, String category) {
            super(id, Metadata.empty());
            this.category = category;
        }

        @Override public String name() { return "test"; }
        @Override public String version() { return "1.0.0"; }
        @Override public String description() { return "desc"; }
        @Override public Set<String> tags() { return Set.of(); }
        @Override public Set<String> categories() { return Set.of(category); }
        @Override public Map<String, ParameterDescriptor> inputs() { return Map.of(); }
        @Override public Map<String, ParameterDescriptor> outputs() { return Map.of(); }
        @Override public List<CapabilityDescriptor> capabilities() { return List.of(); }
        @Override public String category() { return category; }
    }

    static class TestSkill extends BaseResource implements Skill {
        private final SkillDescriptor descriptor;
        private final Function<SkillContext, SkillResult> executor;

        TestSkill(ResourceId id, SkillDescriptor descriptor, Function<SkillContext, SkillResult> executor) {
            super(id, Metadata.empty());
            this.descriptor = descriptor;
            this.executor = executor;
        }

        @Override public SkillDescriptor descriptor() { return descriptor; }
        @Override public SkillResult execute(SkillContext context) { return executor.apply(context); }
    }

    private Skill createMockSkill(String name) {
        ResourceId skillId = new ResourceId.SkillId(Id.random());
        TestDescriptor descriptor = new TestDescriptor(skillId, "test");
        return new TestSkill(skillId, descriptor, context ->
                SkillResult.success(Map.of("name", name, "input", context.getInputs().getOrDefault("key", ""))));
    }

    @Test
    void returnsFailureWhenSkillCannotBeResolved() throws Exception {
        SkillResolver emptyResolver = request -> CompletableFuture.completedFuture(Optional.empty());
        DefaultSkillRuntime runtime = new DefaultSkillRuntime(emptyResolver, Executors.newSingleThreadExecutor());

        SkillExecutionRequest request = SkillExecutionRequest.of(SkillReference.of("non-existent"), Map.of());
        SkillResult result = runtime.execute(request).toCompletableFuture().get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getObservation()).contains("Skill not found: non-existent");
    }

    @Test
    void executesResolvedSkill() throws Exception {
        Skill skill = createMockSkill("echo-skill");
        SkillResolver resolver = request -> CompletableFuture.completedFuture(Optional.of(skill));
        DefaultSkillRuntime runtime = new DefaultSkillRuntime(resolver, Executors.newSingleThreadExecutor());

        SkillExecutionRequest request = SkillExecutionRequest.of(SkillReference.of("echo-skill"), Map.of("key", "value123"));
        SkillResult result = runtime.execute(request).toCompletableFuture().get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOutputs()).containsEntry("name", "echo-skill");
        assertThat(result.getOutputs()).containsEntry("input", "value123");
    }

    @Test
    void createsInvocationId() {
        String id1 = SkillInvocationIds.generate();
        String id2 = SkillInvocationIds.generate();

        assertThat(id1).startsWith("skill-");
        assertThat(id2).startsWith("skill-");
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void convertsSkillExceptionToFailure() throws Exception {
        ResourceId skillId = new ResourceId.SkillId(Id.random());
        TestDescriptor descriptor = new TestDescriptor(skillId, "test");
        Skill failingSkill = new TestSkill(skillId, descriptor, context -> {
            throw new IllegalStateException("Skill internal error");
        });

        SkillResolver resolver = request -> CompletableFuture.completedFuture(Optional.of(failingSkill));
        DefaultSkillRuntime runtime = new DefaultSkillRuntime(resolver, Executors.newSingleThreadExecutor());

        SkillExecutionRequest request = SkillExecutionRequest.of(SkillReference.of("failing-skill"), Map.of());
        SkillResult result = runtime.execute(request).toCompletableFuture().get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
        assertThat(result.getError().getMessage()).contains("Skill internal error");
    }

    @Test
    void respectsSkillTimeout() throws Exception {
        ResourceId skillId = new ResourceId.SkillId(Id.random());
        TestDescriptor descriptor = new TestDescriptor(skillId, "test");
        Skill slowSkill = new TestSkill(skillId, descriptor, context -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            return SkillResult.success(Map.of("done", true));
        });

        SkillResolver resolver = request -> CompletableFuture.completedFuture(Optional.of(slowSkill));
        DefaultSkillRuntime runtime = new DefaultSkillRuntime(resolver, Executors.newSingleThreadExecutor());

        SkillExecutionRequest request = new SkillExecutionRequest(
                SkillReference.of("slow-skill"),
                Map.of(),
                Optional.of(Duration.ofMillis(50)),
                Map.of()
        );

        SkillResult result = runtime.execute(request).toCompletableFuture().get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void contextInputsAreImmutable() {
        DefaultSkillContext context = new DefaultSkillContext(
                Map.of("k", "v"),
                "inv-1",
                "exec-1",
                "trace-1",
                Map.of("attr", "val")
        );

        assertThatThrownBy(() -> context.getInputs().put("k2", "v2"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void registrySkillResolverResolvesRegisteredSkill() throws Exception {
        Skill mockSkill = createMockSkill("registered-skill");
        SkillRegistry mockRegistry = new SkillRegistry() {
            @Override
            public Optional<Skill> findById(String id) {
                if ("registered-skill".equals(id)) {
                    return Optional.of(mockSkill);
                }
                return Optional.empty();
            }

            @Override
            public List<Skill> findByCategory(String category) {
                return List.of();
            }

            @Override
            public void register(Skill resource) {}

            @Override
            public void unregister(ResourceId id) {}

            @Override
            public Optional<Skill> find(ResourceId id) { return Optional.empty(); }

            @Override
            public Optional<Skill> findByName(String name) { return Optional.empty(); }

            @Override
            public List<Skill> findAll() { return List.of(); }

            @Override
            public List<Skill> findByType(ResourceType type) { return List.of(); }

            @Override
            public List<Skill> findByLabel(String key, String value) { return List.of(); }

            @Override
            public boolean exists(ResourceId id) { return false; }

            @Override
            public boolean existsByName(String name) { return false; }

            @Override
            public int count() { return 0; }

            @Override
            public void clear() {}
        };

        RegistrySkillResolver provider = new RegistrySkillResolver(mockRegistry);
        assertThat(provider.id()).isEqualTo("registry");

        Optional<Skill> found = provider.resolve(SkillResolutionRequest.of(SkillReference.of("registered-skill"))).toCompletableFuture().get();
        assertThat(found).isPresent();

        Optional<Skill> notFound = provider.resolve(SkillResolutionRequest.of(SkillReference.of("unknown"))).toCompletableFuture().get();
        assertThat(notFound).isEmpty();
    }

    @Test
    void defaultSkillResolverChainsProviders() throws Exception {
        Skill skillFromSecond = createMockSkill("second-provider-skill");

        SkillProvider provider1 = new SkillProvider() {
            @Override
            public String id() { return "p1"; }
            @Override
            public CompletableFuture<Optional<Skill>> resolve(SkillResolutionRequest request) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
        };

        SkillProvider provider2 = new SkillProvider() {
            @Override
            public String id() { return "p2"; }
            @Override
            public CompletableFuture<Optional<Skill>> resolve(SkillResolutionRequest request) {
                if ("second-provider-skill".equals(request.reference().id())) {
                    return CompletableFuture.completedFuture(Optional.of(skillFromSecond));
                }
                return CompletableFuture.completedFuture(Optional.empty());
            }
        };

        DefaultSkillResolver chainedResolver = new DefaultSkillResolver(List.of(provider1, provider2));

        Optional<Skill> resolved = chainedResolver.resolve(SkillResolutionRequest.of(SkillReference.of("second-provider-skill"))).toCompletableFuture().get();
        assertThat(resolved).isPresent();

        Optional<Skill> unresolvable = chainedResolver.resolve(SkillResolutionRequest.of(SkillReference.of("unknown"))).toCompletableFuture().get();
        assertThat(unresolvable).isEmpty();
    }
}
