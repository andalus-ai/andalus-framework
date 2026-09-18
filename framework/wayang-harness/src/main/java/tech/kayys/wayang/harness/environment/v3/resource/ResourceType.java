package tech.kayys.wayang.harness.environment.v3.resource;

/**
 * Universal classification of environmental resources in v3.0.
 */
public enum ResourceType {
    CPU,
    MEMORY,
    GPU,
    STORAGE,
    FILESYSTEM,
    WORKSPACE,
    PROCESS,
    NETWORK,
    PORT,
    SECRET,
    DEVICE,
    SERVICE,
    CUSTOM
}
