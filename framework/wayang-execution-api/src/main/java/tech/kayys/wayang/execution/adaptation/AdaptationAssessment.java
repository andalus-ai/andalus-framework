package tech.kayys.wayang.execution.adaptation;

import tech.kayys.wayang.execution.mobility.ExecutionIdentity;

import java.util.List;
import java.util.Objects;

/**
 * Assessment determining whether execution adaptation is warranted based on telemetry signals.
 */
public record AdaptationAssessment(
    ExecutionIdentity executionId,
    ExecutionSignal triggeringSignal,
    boolean adaptationRequired,
    List<AdaptationType> feasibleActions
) {
    public AdaptationAssessment {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(triggeringSignal, "triggeringSignal cannot be null");
        feasibleActions = feasibleActions != null ? List.copyOf(feasibleActions) : List.of();
    }

    public static AdaptationAssessment required(ExecutionIdentity executionId, ExecutionSignal signal, List<AdaptationType> actions) {
        return new AdaptationAssessment(executionId, signal, true, actions);
    }

    public static AdaptationAssessment none(ExecutionIdentity executionId, ExecutionSignal signal) {
        return new AdaptationAssessment(executionId, signal, false, List.of());
    }
}
