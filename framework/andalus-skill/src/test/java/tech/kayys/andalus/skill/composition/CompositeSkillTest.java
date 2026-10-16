package tech.kayys.andalus.skill.composition;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.descriptor.CapabilityDescriptor;
import tech.kayys.andalus.descriptor.ParameterDescriptor;
import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.resource.BaseResource;
import tech.kayys.andalus.skill.runtime.DefaultSkillContext;
import tech.kayys.andalus.skill.runtime.SkillExecutionRequest;
import tech.kayys.andalus.skill.runtime.SkillRuntime;
import tech.kayys.andalus.skill.spi.SkillContext;
import tech.kayys.andalus.skill.spi.SkillDescriptor;
import tech.kayys.andalus.skill.spi.SkillReference;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompositeSkillTest {

    static class MockDescriptor extends BaseResource implements SkillDescriptor {
        MockDescriptor(ResourceId id) {
            super(id, Metadata.empty());
        }
        @Override public String name() { return "composite"; }
        @Override public String version() { return "1.0.0"; }
        @Override public String description() { return "desc"; }
        @Override public String category() { return "test"; }
        @Override public Set<String> tags() { return Set.of(); }
        @Override public Set<String> categories() { return Set.of(); }
        @Override public Map<String, ParameterDescriptor> inputs() { return Map.of(); }
        @Override public Map<String, ParameterDescriptor> outputs() { return Map.of(); }
        @Override public List<CapabilityDescriptor> capabilities() { return List.of(); }
    }

    @Test
    void skillStepValidation() {
        SkillReference ref = SkillReference.of("sub-skill");
        SkillStep skillStep = SkillStep.ofSkill("step-1", ref);
        assertThat(skillStep.id()).isEqualTo("step-1");
        assertThat(skillStep.type()).isEqualTo(SkillStepType.SKILL);
        assertThat(skillStep.skill()).contains(ref);

        SkillStep toolStep = SkillStep.ofTool("step-2", "web-search");
        assertThat(toolStep.id()).isEqualTo("step-2");
        assertThat(toolStep.type()).isEqualTo(SkillStepType.TOOL);
        assertThat(toolStep.tool()).contains("web-search");

        assertThatThrownBy(() -> new SkillStep("s", SkillStepType.SKILL, java.util.Optional.empty(), java.util.Optional.empty(), Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void executesSequentialCompositeSkill() throws Exception {
        SkillReference step1Ref = SkillReference.of("fetch-user");
        SkillReference step2Ref = SkillReference.of("format-email");

        Map<String, Map<String, Object>> recordedInputs = new HashMap<>();

        SkillRuntime mockRuntime = request -> {
            recordedInputs.put(request.skill().id(), request.inputs());
            if ("fetch-user".equals(request.skill().id())) {
                return CompletableFuture.completedFuture(
                        SkillResult.success(Map.of("userName", "Alice", "userDomain", "example.com"))
                );
            } else if ("format-email".equals(request.skill().id())) {
                String name = String.valueOf(request.inputs().get("userName"));
                String domain = String.valueOf(request.inputs().get("userDomain"));
                return CompletableFuture.completedFuture(
                        SkillResult.success(Map.of("email", name.toLowerCase() + "@" + domain))
                );
            }
            return CompletableFuture.completedFuture(SkillResult.failure("unknown", "unknown"));
        };

        SkillComposition composition = SkillComposition.of(List.of(
                SkillStep.ofSkill("s1", step1Ref),
                SkillStep.ofSkill("s2", step2Ref)
        ));

        DefaultCompositeSkillRuntime compositeRuntime = new DefaultCompositeSkillRuntime(mockRuntime);

        ResourceId compId = new ResourceId.SkillId(Id.random());
        CompositeSkill compositeSkill = new CompositeSkill(new MockDescriptor(compId), composition, compositeRuntime);

        SkillContext context = new DefaultSkillContext(
                Map.of("userId", "12345"),
                "inv-1",
                "exec-1",
                "trace-1",
                Map.of()
        );

        SkillResult result = compositeSkill.execute(context);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOutputs()).containsEntry("userId", "12345");
        assertThat(result.getOutputs()).containsEntry("userName", "Alice");
        assertThat(result.getOutputs()).containsEntry("email", "alice@example.com");

        assertThat(recordedInputs.get("fetch-user")).containsEntry("userId", "12345");
        assertThat(recordedInputs.get("format-email")).containsEntry("userName", "Alice");
    }

    @Test
    void compositeAbortsOnStepFailure() throws Exception {
        SkillRuntime failingRuntime = request -> CompletableFuture.completedFuture(
                SkillResult.failure(request.skill().id(), "Step failed")
        );

        SkillComposition composition = SkillComposition.of(List.of(
                SkillStep.ofSkill("s1", SkillReference.of("step-fail")),
                SkillStep.ofSkill("s2", SkillReference.of("step-never-run"))
        ));

        DefaultCompositeSkillRuntime compositeRuntime = new DefaultCompositeSkillRuntime(failingRuntime);
        SkillContext context = new DefaultSkillContext(Map.of(), "inv-1", "e-1", "t-1", Map.of());

        SkillResult result = compositeRuntime.execute(composition, context).toCompletableFuture().get();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getObservation()).contains("Step failed");
    }
}
