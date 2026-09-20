package tech.kayys.wayang.memory.graph;

import java.util.List;
import java.util.Optional;

/**
 * Mutable/evolvable working memory graph used during agent reasoning loops.
 */
public interface WorkingMemoryGraph {

    /**
     * Adds or updates a memory node.
     */
    void putNode(MemoryNode node);

    /**
     * Retrieves a node by its identifier.
     */
    Optional<MemoryNode> getNode(String nodeId);

    /**
     * Finds nodes of a specific type (e.g. all verified FACTs or active HYPOTHESEs).
     */
    List<MemoryNode> getNodesByType(MemoryNodeType type);

    /**
     * Connects two nodes with a directed relationship edge.
     */
    void addEdge(MemoryEdge edge);

    /**
     * Gets all outbound edges from a source node.
     */
    List<MemoryEdge> getOutboundEdges(String sourceNodeId);

    /**
     * Generates an immutable snapshot of current memory state for durable checkpointing.
     */
    MemoryGraphSnapshot snapshot(String snapshotId);

    /**
     * Restores graph state from an existing snapshot.
     */
    void restore(MemoryGraphSnapshot snapshot);
}
