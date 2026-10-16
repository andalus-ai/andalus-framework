package tech.kayys.andalus.context.assembly;

import java.util.Map;

/**
 * Contract for serializing an assembled ContextEnvelope into a target format for model ingestion.
 *
 * @param <T> Target serialized format (e.g., String, List of messages, or provider-specific payload)
 */
public interface ContextSerializer<T> {

    T serialize(ContextEnvelope envelope, Map<String, Object> options);

    default T serialize(ContextEnvelope envelope) {
        return serialize(envelope, Map.of());
    }
}
