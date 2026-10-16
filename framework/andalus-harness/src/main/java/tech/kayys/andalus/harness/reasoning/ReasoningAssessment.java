package tech.kayys.andalus.harness.reasoning;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Decision-relevant assessment produced by the {@link ReasoningRuntime}.
 *
 * <p>Mirrors the semantics of {@code tech.kayys.andalus.reasoner.ReasoningResult}
 * from {@code andalus-engine} while operating at the harness level where the
 * engine module is not a compile-time dependency. A
 * {@link LegacyReasonerAdapter} bridges between the two at runtime.</p>
 *
 * <h3>Field semantics</h3>
 * <ul>
 *   <li>{@code conclusion} — concise assessment relevant to the request; always safe to publish</li>
 *   <li>{@code steps} — safe, publishable rationale steps; not private internal reasoning transcripts</li>
 *   <li>{@code alternatives} — material competing interpretations or possible conclusions</li>
 *   <li>{@code evidence} — evidence references, observations, or structured support</li>
 *   <li>{@code metadata} — provider, version, timing, and operational metadata</li>
 *   <li>{@code confidence} — provider-reported value; semantics are provider-defined, not a calibrated probability</li>
 *   <li>{@code purpose} — the {@link ReasoningPurpose} this assessment addresses</li>
 *   <li>{@code reasoningTimeMs} — wall-clock duration of the operation</li>
 *   <li>{@code timestamp} — when the result was produced</li>
 * </ul>
 */
public record ReasoningAssessment(
        String id,
        ReasoningPurpose purpose,
        String conclusion,
        List<String> steps,
        List<String> alternatives,
        Map<String, Object> evidence,
        Map<String, Object> metadata,
        double confidence,
        long reasoningTimeMs,
        Instant timestamp) {

    public ReasoningAssessment {
        if (id == null || id.isBlank()) id = UUID.randomUUID().toString();
        if (purpose == null) throw new IllegalArgumentException("purpose must not be null");
        if (conclusion == null) conclusion = "";
        steps = steps == null ? List.of() : List.copyOf(steps);
        alternatives = alternatives == null ? List.of() : List.copyOf(alternatives);
        evidence = evidence == null ? Map.of() : Map.copyOf(evidence);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
        if (timestamp == null) timestamp = Instant.now();
    }

    // ── factories ──────────────────────────────────────────────────────────────

    /** Minimal factory — conclusion only. */
    public static ReasoningAssessment of(ReasoningPurpose purpose, String conclusion) {
        return new ReasoningAssessment(
                UUID.randomUUID().toString(),
                purpose,
                conclusion,
                List.of(), List.of(), Map.of(), Map.of(),
                1.0, 0L, Instant.now());
    }

    /** Factory with conclusion and rationale steps. */
    public static ReasoningAssessment of(ReasoningPurpose purpose, String conclusion, List<String> steps) {
        return new ReasoningAssessment(
                UUID.randomUUID().toString(),
                purpose,
                conclusion,
                steps, List.of(), Map.of(), Map.of(),
                1.0, 0L, Instant.now());
    }

    /** Factory representing an explicit failure to produce an assessment. */
    public static ReasoningAssessment failed(ReasoningPurpose purpose, String reason) {
        return new ReasoningAssessment(
                UUID.randomUUID().toString(),
                purpose,
                reason,
                List.of(), List.of(),
                Map.of(), Map.of("status", "FAILED"),
                0.0, 0L, Instant.now());
    }

    // ── convenience ───────────────────────────────────────────────────────────

    /** Returns true if the status metadata does not indicate a failure. */
    public boolean isSuccessful() {
        Object status = metadata.get("status");
        return !"FAILED".equals(status);
    }

    public ReasoningAssessment withConfidence(double newConfidence) {
        return new ReasoningAssessment(id, purpose, conclusion, steps, alternatives,
                evidence, metadata, newConfidence, reasoningTimeMs, timestamp);
    }

    public ReasoningAssessment withAlternative(String alternative) {
        List<String> newAlts = new java.util.ArrayList<>(alternatives);
        newAlts.add(alternative);
        return new ReasoningAssessment(id, purpose, conclusion, steps, List.copyOf(newAlts),
                evidence, metadata, confidence, reasoningTimeMs, timestamp);
    }
}
