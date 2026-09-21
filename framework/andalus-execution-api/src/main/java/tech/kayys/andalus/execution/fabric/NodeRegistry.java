package tech.kayys.andalus.execution.fabric;

import java.util.Collection;
import java.util.Optional;

/**
 * Cluster registry maintaining active nodes and their status.
 */
public interface NodeRegistry {

    void register(AndalusNode node);

    void heartbeat(NodeIdentity identity, double currentLoad);

    void deregister(NodeIdentity identity);

    Optional<AndalusNode> get(NodeIdentity identity);

    Collection<AndalusNode> allNodes();
}
