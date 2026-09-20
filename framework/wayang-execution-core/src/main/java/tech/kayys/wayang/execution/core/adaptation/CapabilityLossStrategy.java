package tech.kayys.wayang.execution.core.adaptation;

import tech.kayys.wayang.execution.adaptation.AdaptationPlan;
import tech.kayys.wayang.execution.adaptation.AdaptationStrategy;
import tech.kayys.wayang.execution.adaptation.AdaptationType;
import tech.kayys.wayang.execution.adaptation.ExecutionDelta;
import tech.kayys.wayang.execution.adaptation.ExecutionSignal;
import tech.kayys.wayang.execution.adaptation.RiskAssessment;
import tech.kayys.wayang.execution.adaptation.SignalType;
import tech.kayys.wayang.execution.fabric.FabricSnapshot;
import tech.kayys.wayang.execution.fabric.WayangNode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Strategy responding to loss of capability (e.g. tool API unavailability, revoked credential).
 */
public class CapabilityLossStrategy implements AdaptationStrategy {

    @Override
    public boolean supports(ExecutionSignal signal) {
        return signal != null && signal.type() == SignalType.CAPABILITY_LOSS;
    }

    @Override
    public Optional<AdaptationPlan> formulate(ExecutionSignal signal, WayangNode currentNode, FabricSnapshot fabric) {
        if (!supports(signal)) return Optional.empty();

        List<ExecutionDelta> deltas = List.of(
            ExecutionDelta.of("capability.fallback", "primary", "secondary", "Switch to fallback capability provider")
        );

        AdaptationPlan plan = AdaptationPlan.of(
            "adapt-plan-" + UUID.randomUUID(),
            signal.executionId(),
            AdaptationType.STRATEGY_FALLBACK,
            deltas,
            RiskAssessment.high("Capability fallback requires validation")
        );

        return Optional.of(plan);
    }
}
