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
 * Strategy responding to node degradation or failure signals by planning migration.
 */
public class NodeFailureStrategy implements AdaptationStrategy {

    @Override
    public boolean supports(ExecutionSignal signal) {
        return signal != null && (signal.type() == SignalType.NODE_FAILURE || signal.type() == SignalType.HEALTH_DEGRADATION);
    }

    @Override
    public Optional<AdaptationPlan> formulate(ExecutionSignal signal, AndalusNode currentNode, FabricSnapshot fabric) {
        if (!supports(signal)) return Optional.empty();

        List<ExecutionDelta> deltas = List.of(
            ExecutionDelta.of("execution.location", currentNode != null ? currentNode.identity().canonicalName() : "unknown", "migrated", "Migrate execution off failing node")
        );

        AdaptationPlan plan = AdaptationPlan.of(
            "adapt-plan-" + UUID.randomUUID(),
            signal.executionId(),
            AdaptationType.NODE_MIGRATION,
            deltas,
            RiskAssessment.high("Execution migration required due to node failure")
        );

        return Optional.of(plan);
    }
}
