package tech.kayys.andalus.harness.model;

import java.util.Set;

/**
 * Defines the contract for model descriptor operations in the Andalus framework.
 */


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
