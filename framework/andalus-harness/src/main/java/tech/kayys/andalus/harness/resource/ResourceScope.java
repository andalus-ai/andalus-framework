package tech.kayys.andalus.harness.resource;

/**
 * Boundary contract defining which resources an agent execution may access.
 */
public interface ResourceScope {

    boolean allows(ResourceRequest request);

    ResourceDecision evaluate(ResourceRequest request);
}
