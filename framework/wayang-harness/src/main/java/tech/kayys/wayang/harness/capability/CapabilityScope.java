package tech.kayys.wayang.harness.capability;

/**
 * Evaluates whether an agent execution is authorized to use a given capability in context.
 */
public interface CapabilityScope {

    boolean allows(String capabilityId);

    CapabilityDecision evaluate(CapabilityRequest request);
}
