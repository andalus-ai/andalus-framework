package tech.kayys.andalus.harness.semantics;

/**
 * Standard representation of an executable agent operation in Andalus.
 */
public interface Operation {

    OperationId id();

    OperationType type();

    OperationIntent intent();

    EffectDescriptor effects();

    OperationInput input();

    ExecutionConstraints constraints();
}
