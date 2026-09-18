package tech.kayys.wayang.harness.governance;

import tech.kayys.wayang.harness.environment.HarnessCapabilities;
import tech.kayys.wayang.governance.approval.HarnessApproval;
import tech.kayys.wayang.governance.approval.InMemoryHarnessApproval;
import tech.kayys.wayang.governance.budget.DefaultHarnessBudget;
import tech.kayys.wayang.governance.budget.HarnessBudget;
import tech.kayys.wayang.harness.governance.execution.DefaultHarnessActionExecutor;
import tech.kayys.wayang.harness.governance.execution.HarnessActionExecutor;
import tech.kayys.wayang.governance.policy.DefaultPolicyChain;
import tech.kayys.wayang.governance.policy.HarnessPolicy;

import java.util.Objects;

/**
 * Default implementation of {@link HarnessGovernance}.
 */
public class DefaultHarnessGovernance implements HarnessGovernance {

    private final HarnessCapabilities capabilities;
    private final HarnessPolicy policy;
    private final HarnessApproval approval;
    private final HarnessBudget budget;
    private final HarnessActionExecutor actions;

    public DefaultHarnessGovernance(HarnessCapabilities capabilities) {
        this(capabilities, DefaultPolicyChain.of(), new InMemoryHarnessApproval());
    }

    public DefaultHarnessGovernance(HarnessCapabilities capabilities, HarnessPolicy policy, HarnessApproval approval) {
        this(capabilities, policy, approval, new DefaultHarnessBudget());
    }

    public DefaultHarnessGovernance(HarnessCapabilities capabilities, HarnessPolicy policy, HarnessApproval approval, HarnessBudget budget) {
        this(capabilities, policy, approval, budget, new DefaultHarnessActionExecutor(policy, approval));
    }

    public DefaultHarnessGovernance(HarnessCapabilities capabilities, HarnessPolicy policy, HarnessApproval approval, HarnessBudget budget, HarnessActionExecutor actions) {
        this.capabilities = Objects.requireNonNull(capabilities, "capabilities");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.approval = Objects.requireNonNull(approval, "approval");
        this.budget = budget != null ? budget : new DefaultHarnessBudget();
        this.actions = Objects.requireNonNull(actions, "actions");
    }

    @Override public HarnessCapabilities capabilities() { return capabilities; }
    @Override public HarnessPolicy policy() { return policy; }
    @Override public HarnessApproval approval() { return approval; }
    @Override public HarnessBudget budget() { return budget; }
    @Override public HarnessActionExecutor actions() { return actions; }
}
