package tech.kayys.andalus.harness.governance;

import tech.kayys.andalus.harness.environment.HarnessCapabilities;
import tech.kayys.andalus.governance.approval.HarnessApproval;
import tech.kayys.andalus.governance.budget.HarnessBudget;
import tech.kayys.andalus.harness.governance.execution.HarnessActionExecutor;
import tech.kayys.andalus.governance.policy.HarnessPolicy;

/**
 * Unified governance boundary bundling capability authorization, policies, approvals, budget, and action execution.
 */
public interface HarnessGovernance {

    HarnessCapabilities capabilities();

    HarnessPolicy policy();

    HarnessApproval approval();

    HarnessBudget budget();

    HarnessActionExecutor actions();
}
