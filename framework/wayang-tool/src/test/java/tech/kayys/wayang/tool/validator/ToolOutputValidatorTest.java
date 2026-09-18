package tech.kayys.wayang.tool.validator;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.tool.*;
import tech.kayys.wayang.tool.artifact.ArtifactStore;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ToolOutputValidatorTest {

    @Test
    void shouldPassThroughSmallOutput() {
        DefaultToolOutputValidator validator = new DefaultToolOutputValidator(1024);
        ToolInvocationId id = ToolInvocationId.generate();
        ToolResult result = ToolResult.success(id, "small text", Duration.ofMillis(5), "test");
        ToolExecutionContext ctx = DefaultToolExecutionContext.simple();

        ToolResult validated = validator.validate(result, ctx);
        assertThat(validated.output().truncated()).isFalse();
        assertThat(validated.output().artifactRef()).isEmpty();
        assertThat(validated.output().payload()).isEqualTo("small text");
    }

    @Test
    void shouldTruncateAndStoreLargeOutputInArtifactStore() {
        DefaultToolOutputValidator validator = new DefaultToolOutputValidator(50); // limit 50 bytes
        ToolInvocationId id = ToolInvocationId.generate();
        String largeText = "A".repeat(500);
        ToolResult result = ToolResult.success(id, largeText, Duration.ofMillis(10), "test");

        ArtifactStore artifactStore = ArtifactStore.inMemory();
        ToolExecutionContext ctx = new DefaultToolExecutionContext(
                null, null, null, CancellationToken.none(), ToolLogger.noop(), artifactStore, null
        );

        ToolResult validated = validator.validate(result, ctx);
        assertThat(validated.output().truncated()).isTrue();
        assertThat(validated.output().artifactRef()).isPresent();

        String artifactId = validated.output().artifactRef().get();
        assertThat(artifactStore.retrieve(artifactId)).isPresent();
        assertThat(artifactStore.retrieve(artifactId).get().length).isEqualTo(500);
    }
}
