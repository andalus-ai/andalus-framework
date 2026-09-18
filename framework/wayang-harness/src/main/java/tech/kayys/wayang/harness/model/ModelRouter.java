package tech.kayys.wayang.harness.model;

import java.util.Optional;

/**
 * Defines the contract for model router operations in the Wayang framework.
 */


public interface ModelRouter {

    Optional<ModelResolution> resolve(
            ModelIntent intent,
            ModelRoutingContext context
    );
}
