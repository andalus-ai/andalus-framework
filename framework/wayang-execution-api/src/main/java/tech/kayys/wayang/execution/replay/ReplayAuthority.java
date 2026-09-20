package tech.kayys.wayang.execution.replay;

/**
 * Gatekeeper authorizing replay modes based on safety and security policies.
 */
public interface ReplayAuthority {

    ReplayDecision authorize(String executionId, ReplayManifest manifest);
}
