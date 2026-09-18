package tech.kayys.wayang.harness.contract.model;

import tech.kayys.wayang.harness.contract.schema.SchemaRef;
import java.util.Set;

/**
 * Formal contract specifying result schemas, output types, and artifact requirements.
 */
public record OutputContract(
        SchemaRef resultSchema,
        Set<String> resultTypes,
        Set<String> expectedArtifacts
) {
    public OutputContract {
        resultTypes = resultTypes != null ? Set.copyOf(resultTypes) : Set.of();
        expectedArtifacts = expectedArtifacts != null ? Set.copyOf(expectedArtifacts) : Set.of();
    }

    public static OutputContract of(SchemaRef resultSchema, Set<String> resultTypes) {
        return new OutputContract(resultSchema, resultTypes, Set.of());
    }
}
