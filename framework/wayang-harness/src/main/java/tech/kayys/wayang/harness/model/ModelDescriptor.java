package tech.kayys.wayang.harness.model;

import java.util.Set;

public interface ModelDescriptor {

    ModelId id();

    String name();

    String version();

    Set<ModelTask> tasks();

    ModelCapabilities capabilities();

    ModelLimits limits();

    ModelPricing pricing();

    ModelMetadata metadata();
}
