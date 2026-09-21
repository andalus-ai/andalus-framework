package tech.kayys.andalus.execution.core.adaptation;

import tech.kayys.andalus.execution.adaptation.AdaptationPlan;
import tech.kayys.andalus.execution.adaptation.AdaptationStrategy;
import tech.kayys.andalus.execution.adaptation.AdaptationType;
import tech.kayys.andalus.execution.adaptation.ExecutionDelta;
import tech.kayys.andalus.execution.adaptation.ExecutionSignal;
import tech.kayys.andalus.execution.adaptation.RiskAssessment;
import tech.kayys.andalus.execution.adaptation.SignalType;
import tech.kayys.andalus.execution.fabric.FabricSnapshot;
import tech.kayys.andalus.execution.fabric.AndalusNode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Strategy handling resource pressure signals by scaling resources or proposing migration.
 */
public class ResourcePressureStrategy implements AdaptationStrategy {

    @Override
    public boolean supports(ExecutionSignal signal) {
        return signal != null && signal.type() == SignalType.RESOURCE_PRESSURE;
    }

    @Override
    public Optional<AdaptationPlan> formulate(ExecutionSignal signal, AndalusNode currentNode, FabricSnapshot fabric) {
        if (!supports(signal)) return Optional.empty();

        List<ExecutionDelta> deltas = List.of(
            ExecutionDelta.of("resource.cpu", "current", "expanded", "Scale CPU allocation to alleviate pressure")
        );

        AdaptationPlan plan = AdaptationPlan.of(
            "adapt-plan-" + UUID.randomUUID(),
            signal.executionId(),
            AdaptationType.RESOURCE_RESIZE,
            deltas,
            RiskAssessment.low("Resource scale-up within node capacity")
        );

        return Optional.of(plan);
    }
}
