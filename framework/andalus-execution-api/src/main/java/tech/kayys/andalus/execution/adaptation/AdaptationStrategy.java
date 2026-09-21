package tech.kayys.andalus.execution.adaptation;

import tech.kayys.andalus.execution.fabric.FabricSnapshot;
import tech.kayys.andalus.execution.fabric.AndalusNode;

import java.util.Optional;

/**
 * Pluggable strategy responding to specific execution signals.
 */
public interface AdaptationStrategy {

    boolean supports(ExecutionSignal signal);

    Optional<AdaptationPlan> formulate(ExecutionSignal signal, AndalusNode currentNode, FabricSnapshot fabric);
}
