package tech.kayys.andalus.observability;

import java.util.List;
import java.util.Objects;

public final class CompositeAgentExecutionObserver
        implements AgentExecutionObserver {

    private final List<AgentExecutionObserver> observers;

    public CompositeAgentExecutionObserver(
            List<AgentExecutionObserver> observers) {

        this.observers = observers == null
                ? List.of()
                : observers.stream()
                        .filter(Objects::nonNull)
                        .toList();
    }

    public List<AgentExecutionObserver> getObservers() {
        return observers;
    }

    @Override
    public void onEvent(ObservationEvent event) {
        for (AgentExecutionObserver observer : observers) {
            try {
                observer.onEvent(event);
            } catch (Exception ignored) {
                // Observability failure must not break agent execution
            }
        }
    }

    @Override
    public void onTokenUsage(
            String executionId,
            String traceId,
            TokenUsage usage) {

        for (AgentExecutionObserver observer : observers) {
            try {
                observer.onTokenUsage(
                        executionId,
                        traceId,
                        usage
                );
            } catch (Exception ignored) {
                // Observability failure must not break agent execution
            }
        }
    }

    @Override
    public void onResourceUsage(
            String executionId,
            String traceId,
            ResourceUsage usage) {

        for (AgentExecutionObserver observer : observers) {
            try {
                observer.onResourceUsage(
                        executionId,
                        traceId,
                        usage
                );
            } catch (Exception ignored) {
                // Observability failure must not break agent execution
            }
        }
    }

    @Override
    public void onCostEstimate(
            String executionId,
            String traceId,
            CostEstimate estimate) {

        for (AgentExecutionObserver observer : observers) {
            try {
                observer.onCostEstimate(
                        executionId,
                        traceId,
                        estimate
                );
            } catch (Exception ignored) {
                // Observability failure must not break agent execution
            }
        }
    }

    @Override
    public void onIncident(ExecutionIncident incident) {
        for (AgentExecutionObserver observer : observers) {
            try {
                observer.onIncident(incident);
            } catch (Exception ignored) {
                // Observability failure must not break agent execution
            }
        }
    }
}
