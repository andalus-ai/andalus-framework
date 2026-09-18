package tech.kayys.wayang.harness.protocol;

import java.util.Objects;

/**
 * Represents a completion action.
 *
 * <p>Its components capture `output`.</p>
 *
 * @param output the output
 */


public record CompletionAction(AgentOutput output) implements AgentAction {
    public CompletionAction {
        Objects.requireNonNull(output, "output cannot be null");
    }

    public static CompletionAction of(String summary) {
        return new CompletionAction(AgentOutput.success(summary));
    }
}
