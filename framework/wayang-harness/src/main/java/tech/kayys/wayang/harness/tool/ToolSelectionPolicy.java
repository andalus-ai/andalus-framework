package tech.kayys.wayang.harness.tool;

import java.util.Collection;
import java.util.Optional;

/**
 * Defines the contract for tool selection policy operations in the Wayang framework.
 */


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
