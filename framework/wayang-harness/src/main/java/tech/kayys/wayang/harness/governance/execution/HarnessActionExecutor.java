package tech.kayys.wayang.harness.governance.execution;

import tech.kayys.wayang.harness.governance.action.ActionExecutionResult;
import tech.kayys.wayang.harness.governance.action.HarnessAction;
import tech.kayys.wayang.harness.runtime.HarnessRuntime;

/**
 * Single gate coordinating capability verification, resource authorization, policy evaluation, approval, and execution.
 */
public interface HarnessActionExecutor {

    ActionExecutionResult execute(HarnessAction action, HarnessRuntime runtime);
}
