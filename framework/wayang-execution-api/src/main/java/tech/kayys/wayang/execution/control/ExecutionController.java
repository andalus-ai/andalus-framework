package tech.kayys.wayang.execution.control;

import java.util.Optional;

/**
 * Controller translating ControlCommands into lifecycle transitions.
 */
public interface ExecutionController {

    ControlResult apply(String executionId, ControlCommand command);

    Optional<ExecutionLifecycleState> getState(String executionId);
}
