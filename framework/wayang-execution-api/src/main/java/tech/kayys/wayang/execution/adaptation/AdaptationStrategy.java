package tech.kayys.wayang.execution.adaptation;

import tech.kayys.wayang.execution.fabric.FabricSnapshot;
import tech.kayys.wayang.execution.fabric.WayangNode;

import java.util.Optional;

/**
 * Pluggable strategy responding to specific execution signals.
 */
public interface AdaptationStrategy {

    boolean supports(ExecutionSignal signal);

    Optional<AdaptationPlan> formulate(ExecutionSignal signal, WayangNode currentNode, FabricSnapshot fabric);
}
