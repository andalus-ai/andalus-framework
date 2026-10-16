package tech.kayys.andalus.harness.decision.analytics;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Decision-specific analytics query (Phase 4O).
 *
 * <p>Extends the scope of the existing {@code AnalyticsQuery} with decision-specific
 * dimensions: strategy, model/provider references, governance outcome filter, and
 * turn-level scope. The existing authorization and tenant-scoping rules apply — an
 * analytics filter is not an authorization grant.
 */
public record DecisionAnalyticsQuery(
        Optional<Instant> from,
        Optional<Instant> until,
        List<String> executionIds,
        List<String> turnIds,
        List<String> strategyIds,
        List<String> modelReferences,
        List<String> providerReferences,
        List<String> governanceOutcomes,
        Optional<String> tenantId,
        Optional<String> workspaceId,
        Map<String, Object> filters,
        int limit
) {

    public DecisionAnalyticsQuery {
        from = from == null ? Optional.empty() : from;
        until = until == null ? Optional.empty() : until;
        executionIds = executionIds == null ? List.of() : List.copyOf(executionIds);
        turnIds = turnIds == null ? List.of() : List.copyOf(turnIds);
        strategyIds = strategyIds == null ? List.of() : List.copyOf(strategyIds);
        modelReferences = modelReferences == null ? List.of() : List.copyOf(modelReferences);
        providerReferences = providerReferences == null ? List.of() : List.copyOf(providerReferences);
        governanceOutcomes = governanceOutcomes == null ? List.of() : List.copyOf(governanceOutcomes);
        tenantId = tenantId == null ? Optional.empty() : tenantId;
        workspaceId = workspaceId == null ? Optional.empty() : workspaceId;
        filters = filters == null ? Map.of() : Map.copyOf(filters);
        limit = limit <= 0 ? 100 : limit;
    }

    /** Returns an unbounded query for all decision analytics available to the caller. */
    public static DecisionAnalyticsQuery all() {
        return new DecisionAnalyticsQuery(
                Optional.empty(), Optional.empty(),
                List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                Optional.empty(), Optional.empty(),
                Map.of(), 100);
    }

    /** Returns a query scoped to a single execution. */
    public static DecisionAnalyticsQuery forExecution(String executionId) {
        Objects.requireNonNull(executionId, "executionId must not be null");
        return new DecisionAnalyticsQuery(
                Optional.empty(), Optional.empty(),
                List.of(executionId), List.of(), List.of(), List.of(), List.of(), List.of(),
                Optional.empty(), Optional.empty(),
                Map.of(), 100);
    }

    /** Returns a query filtered to a specific governance outcome (e.g. {@code "DENY"}). */
    public static DecisionAnalyticsQuery forGovernanceOutcome(String outcome) {
        Objects.requireNonNull(outcome, "outcome must not be null");
        return new DecisionAnalyticsQuery(
                Optional.empty(), Optional.empty(),
                List.of(), List.of(), List.of(), List.of(), List.of(), List.of(outcome),
                Optional.empty(), Optional.empty(),
                Map.of(), 100);
    }
}
