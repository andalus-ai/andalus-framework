package tech.kayys.andalus.harness.governance.execution;

import tech.kayys.andalus.governance.action.ActionExecutionResult;
import tech.kayys.andalus.governance.action.HarnessAction;
import tech.kayys.andalus.harness.runtime.HarnessRuntime;

/**
 * Single gate coordinating capability verification, resource authorization, policy evaluation, approval, and execution.
 */
public interface HarnessActionExecutor {

    ActionExecutionResult execute(HarnessAction action, HarnessRuntime runtime);
}
