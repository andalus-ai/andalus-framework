package tech.kayys.wayang.harness.model;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a default model descriptor.
 *
 * <p>Its components capture `id`, `name`, `version`, `tasks`, `capabilities`, and other values.</p>
 *
 * @param id the id
 * @param name the name
 * @param version the version
 * @param tasks the tasks
 * @param capabilities the capabilities
 * @param limits the limits
 * @param pricing the pricing
 * @param metadata the metadata
 */


public record DefaultModelDescriptor(
        ModelId id,
        String name,
        String version,
        Set<ModelTask> tasks,
        ModelCapabilities capabilities,
        ModelLimits limits,
        ModelPricing pricing,
        ModelMetadata metadata
) implements ModelDescriptor {

    public DefaultModelDescriptor {
        Objects.requireNonNull(id, "ModelId cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        if (version == null) version = "1.0.0";
        tasks = tasks != null ? Set.copyOf(tasks) : Set.of(ModelTask.CHAT);
        if (capabilities == null) capabilities = ModelCapabilities.standardChat();
        if (limits == null) limits = ModelLimits.of(128000, 4096);
        if (pricing == null) pricing = ModelPricing.free();
        if (metadata == null) metadata = ModelMetadata.cloud("generic");
    }

    public static Builder builder(ModelId id, String name) {
        return new Builder(id, name);
    }
    /**
     * Builder for constructing default model descriptor instances.
     */


    public static class Builder {
        private final ModelId id;
        private final String name;
        private String version = "1.0.0";
        private Set<ModelTask> tasks = Set.of(ModelTask.CHAT);
        private ModelCapabilities capabilities = ModelCapabilities.standardChat();
        private ModelLimits limits = ModelLimits.of(128000, 4096);
        private ModelPricing pricing = ModelPricing.free();
        private ModelMetadata metadata = ModelMetadata.cloud("generic");

        public Builder(ModelId id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder tasks(Set<ModelTask> tasks) {
            this.tasks = tasks;
            return this;
        }

        public Builder capabilities(ModelCapabilities capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder limits(ModelLimits limits) {
            this.limits = limits;
            return this;
        }

        public Builder pricing(ModelPricing pricing) {
            this.pricing = pricing;
            return this;
        }

        public Builder metadata(ModelMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public DefaultModelDescriptor build() {
            return new DefaultModelDescriptor(id, name, version, tasks, capabilities, limits, pricing, metadata);
        }
    }
}
