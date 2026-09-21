package tech.kayys.andalus.tool;

import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.resource.ResourceType;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public interface ToolInvocation extends Extension {

    String name();

    Map<String, Object> arguments();

    default String invocationId() {
        return name() + "-" + System.nanoTime();
    }

    default ToolInvocationId invocationIdentifier() {
        return ToolInvocationId.of(invocationId());
    }

    default ToolId toolId() {
        return name() != null ? ToolId.of(name()) : null;
    }

    default ToolArguments toolArguments() {
        return arguments() != null ? ToolArguments.of(arguments()) : ToolArguments.empty();
    }

    @Override
    default ResourceId id() {
        UUID uuid;
        try {
            uuid = UUID.fromString(invocationId());
        } catch (IllegalArgumentException e) {
            uuid = UUID.nameUUIDFromBytes(invocationId().getBytes(StandardCharsets.UTF_8));
        }
        return new ResourceId.ToolId(Id.fromUUID(uuid));
    }

    @Override
    default ResourceType type() {
        return new ResourceType.Tool();
    }

    @Override
    default Metadata metadata() {
        return Metadata.builder()
                .name(name())
                .description("Invocation for tool " + name())
                .label("type", "tool-invocation")
                .build();
    }

    static ToolInvocation of(ToolId toolId, ToolArguments arguments) {
        return new DefaultToolInvocation(ToolInvocationId.generate(), toolId, arguments);
    }

    static ToolInvocation of(ToolInvocationId id, ToolId toolId, ToolArguments arguments) {
        return new DefaultToolInvocation(id, toolId, arguments);
    }
}
