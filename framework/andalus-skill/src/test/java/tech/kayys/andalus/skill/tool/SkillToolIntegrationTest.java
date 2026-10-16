package tech.kayys.andalus.skill.tool;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.descriptor.CapabilityDescriptor;
import tech.kayys.andalus.descriptor.ParameterDescriptor;
import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.resource.BaseResource;
import tech.kayys.andalus.skill.runtime.SkillExecutionRequest;
import tech.kayys.andalus.skill.runtime.SkillRuntime;
import tech.kayys.andalus.skill.spi.SkillDescriptor;
import tech.kayys.andalus.skill.spi.SkillReference;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class SkillToolIntegrationTest {

    static class MockSkillDescriptor extends BaseResource implements SkillDescriptor {
        private final String name;
        private final String version;
        private final String description;
        private final String category;

        MockSkillDescriptor(ResourceId id, String name, String version, String description, String category) {
            super(id, Metadata.empty());
            this.name = name;
            this.version = version;
            this.description = description;
            this.category = category;
        }

        @Override public String name() { return name; }
        @Override public String version() { return version; }
        @Override public String description() { return description; }
        @Override public String category() { return category; }
        @Override public Set<String> tags() { return Set.of(); }
        @Override public Set<String> categories() { return Set.of(category); }
        @Override public Map<String, ParameterDescriptor> inputs() { return Map.of(); }
        @Override public Map<String, ParameterDescriptor> outputs() { return Map.of(); }
        @Override public List<CapabilityDescriptor> capabilities() { return List.of(); }
    }

    @Test
    void skillToolDescriptorDerivesFromSkillDescriptor() {
        ResourceId skillId = new ResourceId.SkillId(Id.random());
        SkillDescriptor skillDesc = new MockSkillDescriptor(
                skillId, "Web Search", "1.0.0", "Searches the web", "research");

        SkillToolDescriptor toolDesc = SkillToolDescriptor.from(skillDesc);

        assertThat(toolDesc.id()).isEqualTo(skillId.asString());
        assertThat(toolDesc.name()).isEqualTo("Web Search");
        assertThat(toolDesc.description()).isEqualTo("Searches the web");
        assertThat(toolDesc.version()).isEqualTo("1.0.0");
        assertThat(toolDesc.category()).isEqualTo("research");
        assertThat(toolDesc.source()).isSameAs(skillDesc);
    }

    @Test
    void defaultSkillToolDescriptorFactoryWorks() {
        ResourceId skillId = new ResourceId.SkillId(Id.random());
        SkillToolDescriptorFactory factory = new DefaultSkillToolDescriptorFactory();
        SkillDescriptor skillDesc = new MockSkillDescriptor(
                skillId, "Calculator", "2.0.0", "Performs math", "math");

        SkillToolDescriptor descriptor = factory.create(skillDesc);
        assertThat(descriptor.id()).isEqualTo(skillId.asString());
        assertThat(descriptor.name()).isEqualTo("Calculator");
    }

    @Test
    void skillAsToolBridgeDelegatesToRuntimeWithLineage() throws Exception {
        SkillReference ref = SkillReference.of("skill-summary");
        AtomicReference<SkillExecutionRequest> capturedRequest = new AtomicReference<>();

        SkillRuntime mockRuntime = request -> {
            capturedRequest.set(request);
            return CompletableFuture.completedFuture(SkillResult.success(Map.of("summary", "text")));
        };

        SkillAsToolBridge bridge = new SkillAsToolBridge(ref, mockRuntime);
        assertThat(bridge.skill()).isEqualTo(ref);

        SkillResult result = bridge.execute(
                Map.of("text", "hello world"),
                "exec-99",
                "trace-88",
                "turn-77"
        ).toCompletableFuture().get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOutputs()).containsEntry("summary", "text");

        SkillExecutionRequest request = capturedRequest.get();
        assertThat(request).isNotNull();
        assertThat(request.skill()).isEqualTo(ref);
        assertThat(request.inputs()).containsEntry("text", "hello world");
        assertThat(request.executionId()).isEqualTo("exec-99");
        assertThat(request.traceId()).isEqualTo("trace-88");
        assertThat(request.turnId()).isEqualTo("turn-77");
    }
}
