package tech.kayys.wayang.tool.routing;

/**
 * Checks whether a given tool provider is healthy/available.
 */
public interface ToolProviderAvailability {

    boolean available(String providerId);
}
