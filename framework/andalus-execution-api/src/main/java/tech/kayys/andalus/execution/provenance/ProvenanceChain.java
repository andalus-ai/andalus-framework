package tech.kayys.andalus.execution.provenance;

import java.util.List;
import java.util.Objects;

/**
 * Ordered, content-hashed chain of execution provenance nodes.
 */
public record ProvenanceChain(
        String executionId,
        List<ExecutionNode> nodes,
        String chainDigest
) {
    public ProvenanceChain {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        nodes = nodes == null ? List.of() : List.copyOf(nodes);
        Objects.requireNonNull(chainDigest, "chainDigest cannot be null");
    }
}
