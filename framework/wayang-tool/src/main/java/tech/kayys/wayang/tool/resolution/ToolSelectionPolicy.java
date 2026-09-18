package tech.kayys.wayang.tool.resolution;

import tech.kayys.wayang.tool.ToolDescriptor;

import java.util.Collection;
import java.util.Optional;

@FunctionalInterface
public interface ToolSelectionPolicy {

    Optional<ToolDescriptor> select(
            ToolIntent intent,
            Collection<ToolDescriptor> candidates,
            ToolResolutionContext context
    );

    static ToolSelectionPolicy firstMatch() {
        return (intent, candidates, context) -> candidates.stream().findFirst();
    }
}
