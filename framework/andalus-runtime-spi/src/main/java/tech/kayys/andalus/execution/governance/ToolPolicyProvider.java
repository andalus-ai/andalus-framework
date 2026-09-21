package tech.kayys.andalus.execution.governance;

import java.util.Collection;

/**
 * SPI for providing tool policies to the governance registry or evaluator.
 */
public interface ToolPolicyProvider {

    Collection<? extends ToolPolicy> policies();
}
