package tech.kayys.andalus.spi.agent;

import java.util.Map;
import java.util.Objects;

/**
 * Machine-readable description of agent input/output data.
 *
 * <p>The actual schema can be JSON Schema, Avro, Protobuf, OpenAPI,
 * or another supported schema system.</p>
 */
public record AgentDataContract(
        String schemaId,
        String schemaVersion,
        String mediaType,
        Map<String, Object> metadata
) {

    public AgentDataContract {
        Objects.requireNonNull(schemaId, "schemaId cannot be null");
        Objects.requireNonNull(schemaVersion, "schemaVersion cannot be null");

        mediaType = mediaType == null
                ? "application/json"
                : mediaType;

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public static AgentDataContract json(
            String schemaId,
            String schemaVersion) {

        return new AgentDataContract(
                schemaId,
                schemaVersion,
                "application/json",
                Map.of());
    }

    public static AgentDataContract any() {
        return new AgentDataContract(
                "urn:andalus:schema:any",
                "1",
                "application/json",
                Map.of());
    }
}
