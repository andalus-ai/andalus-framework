package tech.kayys.andalus.execution.adaptation;

import tech.kayys.andalus.execution.fabric.FabricSnapshot;
import tech.kayys.andalus.execution.fabric.AndalusNode;

import java.util.Optional;

/**
 * Adaptation engine evaluating runtime telemetry signals and formulating adaptation plans.
 */
public interface AdaptationEngine {

    AdaptationAssessment assess(ExecutionSignal signal, FabricSnapshot fabric);

    Optional<AdaptationPlan> plan(ExecutionSignal signal, AndalusNode currentNode, FabricSnapshot fabric);
}
