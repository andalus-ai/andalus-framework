package tech.kayys.andalus.observability;

public interface AgentExecutionObserver {

    void onEvent(ObservationEvent event);

    default void onTokenUsage(
            String executionId,
            String traceId,
            TokenUsage usage) {
    }

    default void onResourceUsage(
            String executionId,
            String traceId,
            ResourceUsage usage) {
    }

    default void onCostEstimate(
            String executionId,
            String traceId,
            CostEstimate estimate) {
    }

    default void onIncident(ExecutionIncident incident) {
    }
}
