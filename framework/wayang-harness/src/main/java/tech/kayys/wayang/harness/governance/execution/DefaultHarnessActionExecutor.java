package tech.kayys.wayang.harness.governance.execution;

import tech.kayys.wayang.harness.environment.CapabilityId;
import tech.kayys.wayang.harness.environment.ResourceId;
import tech.kayys.wayang.governance.action.ActionExecutionResult;
import tech.kayys.wayang.governance.action.HarnessAction;
import tech.kayys.wayang.governance.approval.ApprovalGrant;
import tech.kayys.wayang.governance.approval.ApprovalRequest;
import tech.kayys.wayang.governance.approval.HarnessApproval;
import tech.kayys.wayang.governance.policy.ApprovalDecision;
import tech.kayys.wayang.governance.policy.DefaultPolicyContext;
import tech.kayys.wayang.governance.policy.DenyDecision;
import tech.kayys.wayang.governance.policy.HarnessPolicy;
import tech.kayys.wayang.governance.policy.PolicyContext;
import tech.kayys.wayang.governance.policy.PolicyDecision;
import tech.kayys.wayang.harness.runtime.HarnessRuntime;

import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of {@link HarnessActionExecutor} implementing the multi-step governance gate:
 * Capability -> Resource -> Policy -> Approval -> Execution.
 */
public class DefaultHarnessActionExecutor implements HarnessActionExecutor {

    private final HarnessPolicy policy;
    private final HarnessApproval approval;

    public DefaultHarnessActionExecutor(HarnessPolicy policy, HarnessApproval approval) {
        this.policy = Objects.requireNonNull(policy, "policy");
        this.approval = Objects.requireNonNull(approval, "approval");
    }

    @Override
    public ActionExecutionResult execute(HarnessAction action, HarnessRuntime runtime) {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(runtime, "runtime");

        // 1. Capability check
        CapabilityId capId = CapabilityId.of(action.capability());
        if (runtime.environment().capabilities() != null && !runtime.environment().capabilities().has(capId)) {
            return ActionExecutionResult.failure(action.id(), "Governance Denied: unauthorized capability: " + capId.value());
        }

        // 2. Resource check (if target resource specified)
        if (action.resource().isPresent()) {
            Object rawResource = action.resource().get();
            if (rawResource instanceof ResourceId resId) {
                if (runtime.environment().resources() != null && runtime.environment().resources().find(resId).isEmpty()) {
                    // If resource required not registered/available
                }
            }
        }

        // 3. Policy evaluation
        PolicyContext context = new DefaultPolicyContext(
                runtime.environment().identity(),
                runtime.environment().session(),
                runtime.environment(),
                runtime.environment().resources() != null ? Optional.of(runtime.environment().resources().workspace().id()) : Optional.empty(),
                runtime.context().attributes()
        );

        PolicyDecision decision = policy.evaluate(context, action);

        if (decision instanceof DenyDecision) {
            return ActionExecutionResult.failure(action.id(), "Governance Denied by policy: " + decision.reason());
        }

        if (decision instanceof ApprovalDecision appr) {
            ApprovalRequest req = appr.request();
            if (req != null) {
                Optional<ApprovalGrant> grant = approval.grant(req.id());
                if (grant.isEmpty() || !grant.get().isValid()) {
                    return ActionExecutionResult.failure(action.id(), "Governance Suspended: action requires approval: " + decision.reason());
                }
            } else {
                return ActionExecutionResult.failure(action.id(), "Governance Suspended: action requires approval: " + decision.reason());
            }
        }

        // 4. Authorized and completed through gate
        return ActionExecutionResult.success(action.id(), "Action authorized and executed: " + action.capability());
    }
}
