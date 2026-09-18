package tech.kayys.wayang.harness.tool;

import java.util.Optional;

public interface ToolResolver {

    Optional<ToolResolution> resolve(
            ToolIntent intent,
            ToolResolutionContext context
    );
}
