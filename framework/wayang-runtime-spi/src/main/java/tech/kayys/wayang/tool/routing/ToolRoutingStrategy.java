package tech.kayys.wayang.tool.routing;

/**
 * Strategy for selecting among multiple tool providers.
 */
public enum ToolRoutingStrategy {

    FIRST_AVAILABLE,

    LOCAL_FIRST,

    REMOTE_FIRST,

    LEAST_LOAD,

    PRIORITY
}
