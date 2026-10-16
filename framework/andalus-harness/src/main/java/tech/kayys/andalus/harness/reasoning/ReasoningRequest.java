package tech.kayys.andalus.harness.reasoning;

import tech.kayys.andalus.harness.planning.AgentPlan;
import tech.kayys.andalus.harness.protocol.DecisionContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Request for a reasoning assessment over a {@link DecisionContext}.
 *
 * <p>Wraps an immutable decision context, an optional current plan, and a
 * {@link ReasoningPurpose} that guides the reasoning provider towards the
 * assessment the caller needs. Application-specific attributes may be passed
 * in {@code attributes} without expanding the core enum.</p>
 */
public record ReasoningRequest(
        DecisionContext context,
        Optional<AgentPlan> plan,
        ReasoningPurpose purpose,
        Map<String, Object> attributes) {

    public ReasoningRequest {
        Objects.requireNonNull(context, "context is required");
        Objects.requireNonNull(purpose, "purpose is required");
        plan = plan == null ? Optional.empty() : plan;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    /** Minimal factory: context + purpose, no plan. */
    public static ReasoningRequest of(DecisionContext context, ReasoningPurpose purpose) {
        return new ReasoningRequest(context, Optional.empty(), purpose, Map.of());
    }

    /** Factory with an associated plan (e.g. for {@link ReasoningPurpose#EVALUATE_PLAN}). */
    public static ReasoningRequest of(DecisionContext context, AgentPlan plan, ReasoningPurpose purpose) {
        return new ReasoningRequest(context, Optional.of(plan), purpose, Map.of());
    }
}
