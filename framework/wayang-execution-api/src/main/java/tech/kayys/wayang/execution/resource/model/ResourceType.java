package tech.kayys.wayang.execution.resource.model;

/**
 * Standard resource taxonomy for virtual execution resources.
 */
public enum ResourceType {
    COMPUTE,
    MEMORY,
    STORAGE,
    FILESYSTEM,
    NETWORK,
    DEVICE,
    TOOL,
    MODEL,
    SECRET,
    IDENTITY,
    SERVICE
}
