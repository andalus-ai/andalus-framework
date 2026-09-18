package tech.kayys.wayang.harness.protocol;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a human request.
 *
 * <p>Its components capture `prompt`, `details`.</p>
 *
 * @param prompt the prompt
 * @param details the details
 */


public record HumanRequest(
        String prompt,
        Map<String, Object> details
) {
    public HumanRequest {
        Objects.requireNonNull(prompt, "prompt cannot be null");
        details = details != null ? Map.copyOf(details) : Map.of();
    }

    public static HumanRequest of(String prompt) {
        return new HumanRequest(prompt, Map.of());
    }
}
