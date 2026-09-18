package tech.kayys.wayang.tool;

import tech.kayys.wayang.descriptor.CapabilityDescriptor;
import tech.kayys.wayang.descriptor.CapabilityType;
import tech.kayys.wayang.extension.Metadata;
import tech.kayys.wayang.tool.schema.DefaultToolInputSchema;
import tech.kayys.wayang.tool.schema.DefaultToolOutputSchema;
import tech.kayys.wayang.tool.schema.ToolInputSchema;
import tech.kayys.wayang.tool.schema.ToolOutputSchema;

import java.util.*;

public record DefaultToolDescriptor(
        ToolId toolIdentifier,
        String name,
        String description,
        String version,
        Set<String> capabilityKeys,
        ToolInputSchema toolInputSchema,
        ToolOutputSchema outputSchema,
        ToolExecutionProfile executionProfile
) implements ToolDescriptor {

    public DefaultToolDescriptor {
        Objects.requireNonNull(toolIdentifier, "toolIdentifier cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        description = description != null ? description : "";
        version = version != null ? version : "1.0.0";
        capabilityKeys = capabilityKeys != null ? Set.copyOf(capabilityKeys) : Set.of();
        if (toolInputSchema == null) {
            toolInputSchema = DefaultToolInputSchema.empty();
        }
        if (outputSchema == null) {
            outputSchema = DefaultToolOutputSchema.text();
        }
        if (executionProfile == null) {
            executionProfile = ToolExecutionProfile.readOnly(ToolKind.READ);
        }
    }

    @Override
    public String toolId() {
        return toolIdentifier.value();
    }

    @Override
    public ToolId idAsToolId() {
        return toolIdentifier;
    }

    @Override
    public Map<String, Object> inputSchema() {
        return Collections.emptyMap();
    }

    @Override
    public Metadata metadata() {
        return Metadata.builder()
                .name(name)
                .description(description)
                .version(version)
                .label("type", "tool")
                .build();
    }

    @Override
    public List<CapabilityDescriptor> capabilities() {
        return capabilityKeys.stream()
                .map(k -> CapabilityDescriptor.of(k, CapabilityType.TOOL_USE))
                .toList();
    }

    public static Builder builder(ToolId id, String name) {
        return new Builder(id, name);
    }

    public static class Builder {
        private final ToolId id;
        private final String name;
        private String description = "";
        private String version = "1.0.0";
        private Set<String> capabilities = Set.of();
        private ToolInputSchema inputSchema = DefaultToolInputSchema.empty();
        private ToolOutputSchema outputSchema = DefaultToolOutputSchema.text();
        private ToolExecutionProfile executionProfile = ToolExecutionProfile.readOnly(ToolKind.READ);

        public Builder(ToolId id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder capabilities(Set<String> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder inputSchema(ToolInputSchema inputSchema) {
            this.inputSchema = inputSchema;
            return this;
        }

        public Builder outputSchema(ToolOutputSchema outputSchema) {
            this.outputSchema = outputSchema;
            return this;
        }

        public Builder executionProfile(ToolExecutionProfile executionProfile) {
            this.executionProfile = executionProfile;
            return this;
        }

        public DefaultToolDescriptor build() {
            return new DefaultToolDescriptor(id, name, description, version, capabilities, inputSchema, outputSchema, executionProfile);
        }
    }
}
