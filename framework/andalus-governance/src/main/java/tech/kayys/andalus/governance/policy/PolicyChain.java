package tech.kayys.andalus.governance.policy;

import tech.kayys.andalus.governance.action.HarnessAction;

/**
 * Composable sequence of policies evaluated together.
 */
public interface PolicyChain {

    PolicyDecision evaluate(PolicyContext context, HarnessAction action);
}
