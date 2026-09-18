package tech.kayys.wayang.harness.semantics;

/**
 * Standard representation of an executable agent operation in Wayang.
 */
public interface Operation {

    OperationId id();

    OperationType type();

    OperationIntent intent();

    EffectDescriptor effects();

    OperationInput input();

    ExecutionConstraints constraints();
}
