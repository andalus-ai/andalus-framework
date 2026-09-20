package tech.kayys.wayang.execution.adaptation;

import tech.kayys.wayang.execution.fabric.FabricSnapshot;
import tech.kayys.wayang.execution.fabric.WayangNode;

import java.util.Optional;

/**
 * Adaptation engine evaluating runtime telemetry signals and formulating adaptation plans.
 */
public interface AdaptationEngine {

    AdaptationAssessment assess(ExecutionSignal signal, FabricSnapshot fabric);

    Optional<AdaptationPlan> plan(ExecutionSignal signal, WayangNode currentNode, FabricSnapshot fabric);
}
