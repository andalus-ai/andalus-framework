package tech.kayys.andalus.tool;

import tech.kayys.andalus.descriptor.CapabilityDescriptor;
import tech.kayys.andalus.descriptor.Descriptor;
import tech.kayys.andalus.descriptor.ParameterDescriptor;
import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.resource.ResourceType;
import tech.kayys.andalus.tool.schema.DefaultToolInputSchema;
import tech.kayys.andalus.tool.schema.DefaultToolOutputSchema;
import tech.kayys.andalus.tool.schema.ToolInputSchema;
import tech.kayys.andalus.tool.schema.ToolOutputSchema;

import java.nio.charset.StandardCharsets;
import java.util.*;

public interface ToolDescriptor extends Descriptor {

    String name();

    String description();

    String version();

    Map<String, Object> inputSchema();

    default String toolId() {
        return name();
    }

    default ToolId idAsToolId() {
        return ToolId.of(toolId());
    }

    default ToolInputSchema toolInputSchema() {
        return DefaultToolInputSchema.empty();
    }

    default ToolOutputSchema outputSchema() {
        return DefaultToolOutputSchema.text();
    }

    default ToolExecutionProfile executionProfile() {
        return ToolExecutionProfile.readOnly(ToolKind.READ);
    }

    default Set<String> capabilityKeys() {
        return Collections.emptySet();
    }

    @Override
    default ResourceId id() {
        UUID uuid;
        try {
            uuid = UUID.fromString(name());
        } catch (IllegalArgumentException e) {
            uuid = UUID.nameUUIDFromBytes(name().getBytes(StandardCharsets.UTF_8));
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
                .description(description())
                .version(version())
                .label("type", "tool")
                .build();
    }

    @Override
    default Set<String> tags() {
        return Collections.emptySet();
    }

    @Override
    default Set<String> categories() {
        return Collections.emptySet();
    }

    @Override
    default Map<String, ParameterDescriptor> inputs() {
        return Collections.emptyMap();
    }

    @Override
    default Map<String, ParameterDescriptor> outputs() {
        return Collections.emptyMap();
    }

    @Override
    default List<CapabilityDescriptor> capabilities() {
        return Collections.emptyList();
    }
}
