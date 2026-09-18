package tech.kayys.wayang.harness.contract.streaming;

import tech.kayys.wayang.harness.contract.schema.SchemaRef;
import java.time.Instant;

/**
 * Partial result emitted during execution before final completion.
 */
public record PartialResult(
        String taskId,
        SchemaRef schema,
        Object payload,
        Instant timestamp
) {
    public static PartialResult of(String taskId, SchemaRef schema, Object payload) {
        return new PartialResult(taskId, schema, payload, Instant.now());
    }
}
