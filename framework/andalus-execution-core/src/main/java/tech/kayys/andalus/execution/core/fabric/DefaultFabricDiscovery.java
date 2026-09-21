package tech.kayys.andalus.execution.core.fabric;

import tech.kayys.andalus.execution.fabric.FabricDiscovery;
import tech.kayys.andalus.execution.fabric.FabricSnapshot;
import tech.kayys.andalus.execution.fabric.NodeRegistry;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Fabric discovery producing dynamic snapshots from registered nodes.
 */
public class DefaultFabricDiscovery implements FabricDiscovery {

    private final NodeRegistry registry;
    private final String fabricId;

    public DefaultFabricDiscovery(NodeRegistry registry, String fabricId) {
        this.registry = Objects.requireNonNull(registry, "registry cannot be null");
        this.fabricId = Objects.requireNonNull(fabricId, "fabricId cannot be null");
    }

    public DefaultFabricDiscovery(NodeRegistry registry) {
        this(registry, "fabric-default");
    }

    @Override
    public FabricSnapshot captureSnapshot() {
        return new FabricSnapshot(
            fabricId,
            Instant.now(),
            new ArrayList<>(registry.allNodes()),
            Map.of("totalRegistered", registry.allNodes().size())
        );
    }
}
