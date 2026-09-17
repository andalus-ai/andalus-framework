package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolInvocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation of {@link ToolPolicyEvaluator} that orders policies by ascending priority,
 * short-circuits on {@link PolicyDecision.Deny}, and aggregates decisions with {@link PolicyDecisionCombiner}.
 */
public final class DefaultToolPolicyEvaluator implements ToolPolicyEvaluator {

    private final List<ToolPolicy> policies;

    public DefaultToolPolicyEvaluator(List<? extends ToolPolicy> policies) {
        Objects.requireNonNull(policies, "policies cannot be null");

        List<ToolPolicy> copy = new ArrayList<>(policies.size());
        for (ToolPolicy policy : policies) {
            copy.add(Objects.requireNonNull(policy, "policy cannot be null"));
        }

        copy.sort(Comparator.comparingInt(ToolPolicy::priority));
        this.policies = List.copyOf(copy);
    }

    @Override
    public PolicyDecision evaluate(ToolInvocation invocation, ToolPermissionContext context) {
        return evaluateDetailed(invocation, context).decision();
    }

    public PolicyDecision evaluate(PolicyEvaluationContext context) {
        return evaluateDetailed(context).decision();
    }

    public PolicyEvaluationResult evaluateDetailed(ToolInvocation invocation, ToolPermissionContext context) {
        Objects.requireNonNull(invocation, "invocation cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        return evaluateDetailed(PolicyEvaluationContexts.create(context, invocation));
    }

    public PolicyEvaluationResult evaluateDetailed(PolicyEvaluationContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        PolicyDecision aggregate = PolicyDecision.allow();
        List<PolicyEvaluation> evaluations = new ArrayList<>(policies.size());

        for (ToolPolicy policy : policies) {
            PolicyDecision decision = policy.evaluate(context);

            if (decision == null) {
                throw new IllegalStateException("Policy returned null decision: " + policy.id());
            }

            evaluations.add(new PolicyEvaluation(policy.id(), policy.priority(), decision));

            if (decision instanceof PolicyDecision.Deny || decision.isDenied()) {
                return new PolicyEvaluationResult(decision, evaluations);
            }

            aggregate = PolicyDecisionCombiner.combine(aggregate, decision);
        }

        return new PolicyEvaluationResult(aggregate, evaluations);
    }

    @Override
    public List<ToolPolicy> policies() {
        return policies;
    }
}
