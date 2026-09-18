package tech.kayys.wayang.harness.governance;

import tech.kayys.wayang.harness.environment.HarnessCapabilities;
import tech.kayys.wayang.governance.approval.HarnessApproval;
import tech.kayys.wayang.governance.budget.HarnessBudget;
import tech.kayys.wayang.harness.governance.execution.HarnessActionExecutor;
import tech.kayys.wayang.governance.policy.HarnessPolicy;

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
