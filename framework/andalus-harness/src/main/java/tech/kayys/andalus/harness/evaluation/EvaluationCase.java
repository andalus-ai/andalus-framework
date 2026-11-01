package tech.kayys.andalus.harness.evaluation;

import java.util.Map;
import java.util.Objects;

/**
 * One deterministic evaluation example. Inputs and expected outputs are deliberately
 * opaque strings so evaluators can support prompts, serialized payloads, or references.
 */
public record EvaluationCase(
        String id,
        String input,
        String expectedOutput,
        Map<String, String> metadata
) {
    public EvaluationCase {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id is required");
        if (input == null) throw new IllegalArgumentException("input is required");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
