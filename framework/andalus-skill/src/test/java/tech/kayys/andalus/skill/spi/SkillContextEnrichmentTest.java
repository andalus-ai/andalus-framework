package tech.kayys.andalus.skill.spi;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.skill.runtime.DefaultSkillContext;
import tech.kayys.andalus.skill.runtime.SkillExecutionRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkillContextEnrichmentTest {

    @Test
    void shouldPropagateExecutionLineage() {
        SkillRuntimeConstraints constraints = new SkillRuntimeConstraints(
                Duration.ofSeconds(10), 1024 * 1024, 5000, 2048, Map.of("env", "test"));

        DefaultSkillContext context = new DefaultSkillContext(
                Map.of("query", "hello"),
                "inv-123",
                "exec-456",
                "trace-789",
                "turn-01",
                Map.of("source", "agent-loop"),
                constraints);

        assertThat(context.getInvocationId()).isEqualTo("inv-123");
        assertThat(context.getExecutionId()).isEqualTo("exec-456");
        assertThat(context.getTraceId()).isEqualTo("trace-789");
        assertThat(context.getTurnId()).isEqualTo("turn-01");
        assertThat(context.turnIdOptional()).contains("turn-01");
        assertThat(context.executionIdOptional()).contains("exec-456");
        assertThat(context.getInputs()).containsEntry("query", "hello");
        assertThat(context.getAttributes()).containsEntry("source", "agent-loop");
        assertThat(context.constraints()).isEqualTo(constraints);
    }

    @Test
    void nullInputsBecomeImmutableEmptyMap() {
        DefaultSkillContext context = new DefaultSkillContext(
                null, "inv-1", "exec-1", "trace-1", Map.of());

        assertThat(context.getInputs()).isEmpty();
        assertThatThrownBy(() -> context.getInputs().put("k", "v"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void inputsCannotBeMutated() {
        Map<String, Object> modifiable = new HashMap<>();
        modifiable.put("initial", "val");

        DefaultSkillContext context = new DefaultSkillContext(
                modifiable, "inv-1", "exec-1", "trace-1", Map.of());

        // Mutating source map must not affect context
        modifiable.put("initial", "changed");
        assertThat(context.getInputs().get("initial")).isEqualTo("val");

        // Mutating context map must throw
        assertThatThrownBy(() -> context.getInputs().put("new", "val"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void attributesAreImmutable() {
        Map<String, Object> modifiable = new HashMap<>();
        modifiable.put("attr", "val");

        DefaultSkillContext context = new DefaultSkillContext(
                Map.of(), "inv-1", "exec-1", "trace-1", modifiable);

        modifiable.put("attr", "tampered");
        assertThat(context.getAttributes().get("attr")).isEqualTo("val");
    }

    @Test
    void nullConstraintsUseUnlimitedDefaults() {
        DefaultSkillContext context = new DefaultSkillContext(
                Map.of(), "inv-1", "exec-1", "trace-1", "", Map.of(), null);

        assertThat(context.constraints()).isEqualTo(SkillRuntimeConstraints.unlimited());
        assertThat(context.constraints().timeout()).isEqualTo(Duration.ZERO);
        assertThat(context.constraints().maxMemoryBytes()).isEqualTo(0L);
    }

    @Test
    void negativeTimeoutThrows() {
        assertThatThrownBy(() -> new SkillRuntimeConstraints(
                Duration.ofSeconds(-5), 0, 0, 0, Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("timeout");
    }

    @Test
    void negativeMaxMemoryThrows() {
        assertThatThrownBy(() -> new SkillRuntimeConstraints(
                Duration.ZERO, -1, 0, 0, Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maxMemoryBytes");
    }

    @Test
    void backwardCompatibilityDefaultsOnSkillContext() {
        SkillContext legacy = () -> Map.of("foo", "bar");

        assertThat(legacy.getInputs()).containsEntry("foo", "bar");
        assertThat(legacy.getInvocationId()).isEmpty();
        assertThat(legacy.getExecutionId()).isEmpty();
        assertThat(legacy.getTraceId()).isEmpty();
        assertThat(legacy.getTurnId()).isEmpty();
        assertThat(legacy.turnIdOptional()).isEmpty();
        assertThat(legacy.executionIdOptional()).isEmpty();
        assertThat(legacy.getAttributes()).isEmpty();
        assertThat(legacy.service(String.class)).isEmpty();
    }

    @Test
    void skillExecutionRequestCarriesLineage() {
        SkillReference ref = SkillReference.of("skill-abc");
        SkillExecutionRequest req = SkillExecutionRequest.of(
                ref, Map.of("in", 1), "exec-10", "trace-20", "turn-30");

        assertThat(req.skill()).isEqualTo(ref);
        assertThat(req.inputs()).containsEntry("in", 1);
        assertThat(req.executionId()).isEqualTo("exec-10");
        assertThat(req.traceId()).isEqualTo("trace-20");
        assertThat(req.turnId()).isEqualTo("turn-30");
        assertThat(req.timeout()).isEmpty();
    }
}
