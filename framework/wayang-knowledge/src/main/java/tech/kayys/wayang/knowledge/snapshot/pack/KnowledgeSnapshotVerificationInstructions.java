package tech.kayys.wayang.knowledge.snapshot.pack;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge snapshot verification instructions.
 *
 * <p>Its components capture `protocol version`, `required algorithms`, `required resources`, `requires original runtime`, `requires knowledge store`, and other values.</p>
 *
 * @param protocolVersion the protocol version
 * @param requiredAlgorithms the required algorithms
 * @param requiredResources the required resources
 * @param requiresOriginalRuntime the requires original runtime
 * @param requiresKnowledgeStore the requires knowledge store
 * @param requiresNetwork the requires network
 * @param verificationProcedure the verification procedure
 * @param metadata the metadata
 */


public record KnowledgeSnapshotVerificationInstructions(
        String protocolVersion,
        List<String> requiredAlgorithms,
        List<String> requiredResources,
        boolean requiresOriginalRuntime,
        boolean requiresKnowledgeStore,
        boolean requiresNetwork,
        String verificationProcedure,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotVerificationInstructions {
        requiredAlgorithms = requiredAlgorithms == null ? List.of() : List.copyOf(requiredAlgorithms);
        requiredResources = requiredResources == null ? List.of() : List.copyOf(requiredResources);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
