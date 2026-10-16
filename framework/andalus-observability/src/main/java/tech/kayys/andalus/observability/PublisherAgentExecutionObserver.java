package tech.kayys.andalus.observability;

import java.util.Map;
import java.util.Objects;

public class PublisherAgentExecutionObserver implements AgentExecutionObserver {

    private final ObservabilityPublisher publisher;

    public PublisherAgentExecutionObserver(ObservabilityPublisher publisher) {
        this.publisher = Objects.requireNonNull(publisher, "publisher must not be null");
    }

    @Override
    public void onEvent(ObservationEvent event) {
        publisher.publish(event);
    }

    @Override
    public void onTokenUsage(String executionId, String traceId, TokenUsage usage) {
        if (usage != null) {
            publisher.publish(new ObservationEvent(
                    null,
                    executionId,
                    traceId,
                    ObservabilityEventType.TOKEN_USAGE.name(),
                    null,
                    Map.of(
                            "inputTokens", usage.inputTokens(),
                            "outputTokens", usage.outputTokens(),
                            "cachedInputTokens", usage.cachedInputTokens(),
                            "reasoningTokens", usage.reasoningTokens(),
                            "totalTokens", usage.totalTokens()
                    )
            ));
        }
    }

    @Override
    public void onResourceUsage(String executionId, String traceId, ResourceUsage usage) {
        if (usage != null) {
            publisher.publish(new ObservationEvent(
                    null,
                    executionId,
                    traceId,
                    ObservabilityEventType.RESOURCE_USAGE.name(),
                    null,
                    Map.of(
                            "cpuTimeMillis", usage.cpuTimeMillis(),
                            "memoryBytes", usage.memoryBytes(),
                            "networkBytesIn", usage.networkBytesIn(),
                            "networkBytesOut", usage.networkBytesOut(),
                            "filesystemBytesRead", usage.filesystemBytesRead(),
                            "filesystemBytesWritten", usage.filesystemBytesWritten(),
                            "additional", usage.additional()
                    )
            ));
        }
    }

    @Override
    public void onCostEstimate(String executionId, String traceId, CostEstimate estimate) {
        if (estimate != null) {
            publisher.publish(new ObservationEvent(
                    null,
                    executionId,
                    traceId,
                    ObservabilityEventType.COST_ESTIMATE.name(),
                    null,
                    Map.of(
                            "amount", estimate.amount(),
                            "currency", estimate.currency(),
                            "basis", estimate.basis(),
                            "breakdown", estimate.breakdown()
                    )
            ));
        }
    }

    @Override
    public void onIncident(ExecutionIncident incident) {
        if (incident != null) {
            publisher.publish(new ObservationEvent(
                    incident.id(),
                    incident.executionId(),
                    incident.traceId(),
                    ObservabilityEventType.INCIDENT.name(),
                    incident.timestamp(),
                    Map.of(
                            "severity", incident.severity() != null ? incident.severity().name() : "INFO",
                            "category", incident.category() != null ? incident.category() : "UNKNOWN",
                            "message", incident.message() != null ? incident.message() : "",
                            "attributes", incident.attributes()
                    )
            ));
        }
    }
}
