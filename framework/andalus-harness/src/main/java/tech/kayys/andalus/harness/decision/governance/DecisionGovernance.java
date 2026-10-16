package tech.kayys.andalus.harness.decision.governance;

import java.util.concurrent.CompletionStage;

/**
 * Service contract for governing agent decisions before execution.
 * <p>
 * Decision governance determines whether a proposed decision may proceed under
 * applicable structural, constraint, capability, and policy conditions.
 * It does not replace authorization, policy enforcement, or execution controls.
 */
public interface DecisionGovernance {

    /**
     * Evaluates a proposed decision against governance constraints and policies.
     *
     * @param request the governance evaluation request
     * @return completion stage yielding the governance outcome and findings
     */
    CompletionStage<DecisionGovernanceResult> evaluate(DecisionGovernanceRequest request);
}
