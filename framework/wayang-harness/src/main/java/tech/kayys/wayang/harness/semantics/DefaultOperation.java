package tech.kayys.wayang.harness.semantics;

import java.util.Objects;

/**
 * Immutable reference record implementing {@link Operation}.
 */
public record DefaultOperation(
        OperationId id,
        OperationType type,
        OperationIntent intent,
        EffectDescriptor effects,
        OperationInput input,
        ExecutionConstraints constraints
) implements Operation {

    public DefaultOperation {
        Objects.requireNonNull(id, "OperationId cannot be null");
        Objects.requireNonNull(type, "OperationType cannot be null");
        Objects.requireNonNull(intent, "OperationIntent cannot be null");
        effects = effects != null ? effects : DefaultEffectDescriptor.builder().build();
        input = input != null ? input : OperationInput.empty();
        constraints = constraints != null ? constraints : ExecutionConstraints.defaults();
    }

    public static DefaultOperation of(
            OperationType type,
            OperationIntent intent,
            EffectDescriptor effects
    ) {
        return new DefaultOperation(
                OperationId.generate(),
                type,
                intent,
                effects,
                OperationInput.empty(),
                ExecutionConstraints.defaults()
        );
    }
}
