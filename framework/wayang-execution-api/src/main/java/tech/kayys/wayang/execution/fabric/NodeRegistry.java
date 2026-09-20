package tech.kayys.wayang.execution.fabric;

import java.util.Collection;
import java.util.Optional;

/**
 * Cluster registry maintaining active nodes and their status.
 */
public interface NodeRegistry {

    void register(WayangNode node);

    void heartbeat(NodeIdentity identity, double currentLoad);

    void deregister(NodeIdentity identity);

    Optional<WayangNode> get(NodeIdentity identity);

    Collection<WayangNode> allNodes();
}
