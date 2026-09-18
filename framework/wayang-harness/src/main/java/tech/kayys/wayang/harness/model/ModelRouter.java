package tech.kayys.wayang.harness.model;

import java.util.Optional;

public interface ModelRouter {

    Optional<ModelResolution> resolve(
            ModelIntent intent,
            ModelRoutingContext context
    );
}
