package tech.kayys.andalus.execution.fabric;

/**
 * Service discovering the active topology and generating fabric snapshots.
 */
public interface FabricDiscovery {

    FabricSnapshot captureSnapshot();
}
