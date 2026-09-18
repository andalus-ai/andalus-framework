package tech.kayys.wayang.tool;

import tech.kayys.wayang.tool.artifact.ArtifactStore;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record DefaultToolExecutionContext(
        String executionId,
        Object identity,
        Object resources,
        CancellationToken cancellation,
        ToolLogger logger,
        ArtifactStore artifacts,
        Map<String, Object> attributes
) implements ToolExecutionContext {

    public DefaultToolExecutionContext {
        if (executionId == null) {
            executionId = "exec-" + UUID.randomUUID();
        }
        if (cancellation == null) {
            cancellation = CancellationToken.none();
        }
        if (logger == null) {
            logger = ToolLogger.noop();
        }
        if (artifacts == null) {
            artifacts = ArtifactStore.inMemory();
        }
        attributes = attributes != null ? Map.copyOf(attributes) : Collections.emptyMap();
    }

    public static DefaultToolExecutionContext of(String executionId, Object identity, Object resources) {
        return new DefaultToolExecutionContext(
                executionId,
                identity,
                resources,
                CancellationToken.create(),
                ToolLogger.noop(),
                ArtifactStore.inMemory(),
                Collections.emptyMap()
        );
    }

    public static DefaultToolExecutionContext simple() {
        return new DefaultToolExecutionContext(null, null, null, null, null, null, null);
    }

    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }

    @Override
    public Optional<Object> getAttribute(String key) {
        return Optional.ofNullable(attributes.get(key));
    }
}
