package tech.kayys.wayang.execution.core.sandbox;

import tech.kayys.wayang.execution.sandbox.ExecutionSandbox;
import tech.kayys.wayang.execution.sandbox.SandboxId;
import tech.kayys.wayang.execution.sandbox.SandboxSpec;
import tech.kayys.wayang.execution.sandbox.SandboxState;

import java.util.Objects;

/**
 * Immutable execution sandbox instance.
 */
public record DefaultExecutionSandbox(
        SandboxId id,
        SandboxSpec specification,
        SandboxState state
) implements ExecutionSandbox {

    public DefaultExecutionSandbox {
        Objects.requireNonNull(id, "SandboxId cannot be null");
        Objects.requireNonNull(specification, "SandboxSpec cannot be null");
        state = state != null ? state : SandboxState.CREATING;
    }

    public DefaultExecutionSandbox withState(SandboxState newState) {
        return new DefaultExecutionSandbox(id, specification, newState);
    }
}
