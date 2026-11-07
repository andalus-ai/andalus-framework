package tech.kayys.andalus.reasoner;

import java.util.Objects;

import tech.kayys.andalus.execution.GraphExecutionContext;
import tech.kayys.andalus.planner.Plan;

/** Immutable input to one coordinated reasoning operation. */
public record ReasoningRequest(
        ReasoningStrategy strategy,
        Plan plan,
        GraphExecutionContext graphContext,
        ReasoningExecutionContext executionContext) {

    public ReasoningRequest {
        Objects.requireNonNull(strategy, "strategy must not be null");
        Objects.requireNonNull(plan, "plan must not be null");
        Objects.requireNonNull(graphContext, "graphContext must not be null");
        Objects.requireNonNull(executionContext, "executionContext must not be null");
    }
}
