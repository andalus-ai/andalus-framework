package tech.kayys.wayang.harness.context;

/**
 * Identifies the principal, tenant, agent, and execution scope within the Harness.
 */
public interface HarnessIdentity {

    String agentId();

    String tenantId();

    String userId();

    String executionId();

    String namespace();
}
