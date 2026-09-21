package tech.kayys.andalus.harness.model;

import java.util.Optional;

/**
 * Defines the contract for model router operations in the Andalus framework.
 */


public interface ModelRouter {

    Optional<ModelResolution> resolve(
            ModelIntent intent,
            ModelRoutingContext context
    );
}
