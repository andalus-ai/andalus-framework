package tech.kayys.andalus.execution.core.adaptation;

import tech.kayys.andalus.execution.adaptation.AdaptationAssessment;
import tech.kayys.andalus.execution.adaptation.AdaptationEngine;
import tech.kayys.andalus.execution.adaptation.AdaptationPlan;
import tech.kayys.andalus.execution.adaptation.AdaptationStrategy;
import tech.kayys.andalus.execution.adaptation.AdaptationType;
import tech.kayys.andalus.execution.adaptation.ExecutionSignal;
import tech.kayys.andalus.execution.fabric.FabricSnapshot;
import tech.kayys.andalus.execution.fabric.AndalusNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Default adaptation engine coordinating strategies and evaluating execution telemetry signals.
 */
public class DefaultAdaptationEngine implements AdaptationEngine {

    private final List<AdaptationStrategy> strategies;

    public DefaultAdaptationEngine(List<AdaptationStrategy> strategies) {
        this.strategies = strategies != null ? List.copyOf(strategies) : List.of();
    }

    public DefaultAdaptationEngine() {
        this(List.of(
            new ResourcePressureStrategy(),
            new NodeFailureStrategy(),
            new CapabilityLossStrategy()
        ));
    }

    @Override
    public AdaptationAssessment assess(ExecutionSignal signal, FabricSnapshot fabric) {
        Objects.requireNonNull(signal, "signal cannot be null");

        List<AdaptationType> actions = new ArrayList<>();
        switch (signal.type()) {
            case RESOURCE_PRESSURE -> actions.add(AdaptationType.RESOURCE_RESIZE);
            case NODE_FAILURE, HEALTH_DEGRADATION -> actions.add(AdaptationType.NODE_MIGRATION);
            case CAPABILITY_LOSS -> actions.add(AdaptationType.STRATEGY_FALLBACK);
            case BUDGET_EXHAUSTION -> actions.add(AdaptationType.BUDGET_THROTTLE);
            case MAINTENANCE_SCHEDULED -> actions.add(AdaptationType.NODE_MIGRATION);
            default -> {}
        }

        if (actions.isEmpty()) {
            return AdaptationAssessment.none(signal.executionId(), signal);
        }

        return AdaptationAssessment.required(signal.executionId(), signal, actions);
    }

    @Override
    public Optional<AdaptationPlan> plan(ExecutionSignal signal, AndalusNode currentNode, FabricSnapshot fabric) {
        Objects.requireNonNull(signal, "signal cannot be null");

        for (AdaptationStrategy strategy : strategies) {
            if (strategy.supports(signal)) {
                Optional<AdaptationPlan> plan = strategy.formulate(signal, currentNode, fabric);
                if (plan.isPresent()) {
                    return plan;
                }
            }
        }

        return Optional.empty();
    }
}
