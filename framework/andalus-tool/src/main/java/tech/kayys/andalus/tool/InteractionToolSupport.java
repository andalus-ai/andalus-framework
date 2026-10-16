
package tech.kayys.andalus.tool;

import tech.kayys.andalus.agent.kernel.interaction.InteractionContext;

import java.util.Optional;

public final class InteractionToolSupport {

    private InteractionToolSupport() {
    }

    public static Optional<InteractionContext> interaction(
            ToolContext context
    ) {
        if (context == null) {
            return Optional.empty();
        }

        return context.getAttribute(
                ToolContextKeys.INTERACTION
        ).filter(
                InteractionContext.class::isInstance
        ).map(
                InteractionContext.class::cast
        );
    }

    public static Optional<String> runId(
            ToolContext context
    ) {
        if (context == null) {
            return Optional.empty();
        }

        return context.getAttribute(
                ToolContextKeys.RUN_ID
        ).map(String::valueOf);
    }

    public static Optional<String> executionId(
            ToolContext context
    ) {
        if (context == null) {
            return Optional.empty();
        }

        return context.getAttribute(
                ToolContextKeys.EXECUTION_ID
        ).map(String::valueOf);
    }
}
