package tech.kayys.wayang.tool.resolution;

import java.util.Optional;

public interface ToolResolver {

    Optional<ToolResolution> resolve(ToolIntent intent, ToolResolutionContext context);
}
