package tech.kayys.wayang.harness.tool;

import java.util.Objects;
import java.util.Set;

public record DefaultToolDescriptor(
        ToolId id,
        String name,
        String version,
        Set<String> capabilities,
        ToolInputSchema inputSchema,
        ToolOutputSchema outputSchema,
        ToolExecutionProfile executionProfile
) implements ToolDescriptor {

    public DefaultToolDescriptor {
        Objects.requireNonNull(id, "ToolId cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        if (version == null) {
            version = "1.0.0";
        }
        capabilities = capabilities != null ? Set.copyOf(capabilities) : Set.of();
        if (inputSchema == null) {
            inputSchema = DefaultToolInputSchema.empty();
        }
        if (outputSchema == null) {
            outputSchema = DefaultToolOutputSchema.text();
        }
        if (executionProfile == null) {
            executionProfile = ToolExecutionProfile.readOnly(ToolKind.READ);
        }
    }

    public static Builder builder(ToolId id, String name) {
        return new Builder(id, name);
    }

    public static class Builder {
        private final ToolId id;
        private final String name;
        private String version = "1.0.0";
        private Set<String> capabilities = Set.of();
        private ToolInputSchema inputSchema = DefaultToolInputSchema.empty();
        private ToolOutputSchema outputSchema = DefaultToolOutputSchema.text();
        private ToolExecutionProfile executionProfile = ToolExecutionProfile.readOnly(ToolKind.READ);

        public Builder(ToolId id, String name) {
            this.id = id;
            this.name = name;
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
            return new DefaultToolDescriptor(id, name, version, capabilities, inputSchema, outputSchema, executionProfile);
        }
    }
}
