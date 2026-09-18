package tech.kayys.wayang.harness.contract.model;

import tech.kayys.wayang.harness.contract.schema.SchemaRef;
import java.util.Set;

/**
 * Formal contract specifying accepted task types, input schema, and required context descriptors.
 */
public record InputContract(
        Set<String> acceptedTasks,
        SchemaRef inputSchema,
        Set<String> requiredContext
) {
    public InputContract {
        acceptedTasks = acceptedTasks != null ? Set.copyOf(acceptedTasks) : Set.of();
        requiredContext = requiredContext != null ? Set.copyOf(requiredContext) : Set.of();
    }

    public static InputContract of(Set<String> tasks, SchemaRef schema) {
        return new InputContract(tasks, schema, Set.of());
    }
}
