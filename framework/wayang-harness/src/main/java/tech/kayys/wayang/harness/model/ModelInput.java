package tech.kayys.wayang.harness.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a model input.
 *
 * <p>Its components capture `prompt`, `messages`, `system instructions`, `structured schema`.</p>
 *
 * @param prompt the prompt
 * @param messages the messages
 * @param systemInstructions the system instructions
 * @param structuredSchema the structured schema
 */


public record ModelInput(
        String prompt,
        List<Map<String, Object>> messages,
        Map<String, Object> systemInstructions,
        Optional<String> structuredSchema
) {
    public ModelInput {
        messages = messages != null ? List.copyOf(messages) : List.of();
        systemInstructions = systemInstructions != null ? Map.copyOf(systemInstructions) : Map.of();
        if (structuredSchema == null) {
            structuredSchema = Optional.empty();
        }
    }

    public static ModelInput of(String prompt) {
        return new ModelInput(prompt, List.of(), Map.of(), Optional.empty());
    }

    public static ModelInput chat(List<Map<String, Object>> messages) {
        return new ModelInput(null, messages, Map.of(), Optional.empty());
    }
}
