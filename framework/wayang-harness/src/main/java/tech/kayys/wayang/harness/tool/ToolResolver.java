package tech.kayys.wayang.harness.tool;

import java.util.Optional;

/**
 * Defines the contract for tool resolver operations in the Wayang framework.
 */


public interface ToolResolver {

    Optional<ToolResolution> resolve(
            ToolIntent intent,
            ToolResolutionContext context
    );
}
