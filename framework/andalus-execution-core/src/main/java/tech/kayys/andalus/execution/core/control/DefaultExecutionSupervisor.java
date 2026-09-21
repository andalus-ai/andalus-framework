package tech.kayys.andalus.execution.core.control;

import tech.kayys.andalus.execution.control.ControlAction;
import tech.kayys.andalus.execution.control.ExecutionLifecycleState;
import tech.kayys.andalus.execution.control.ExecutionSupervisor;
import tech.kayys.andalus.execution.control.SupervisionContext;
import tech.kayys.andalus.execution.control.SupervisionDecision;

import java.time.Duration;
import java.util.Objects;

/**
 * Standard implementation of {@link ExecutionSupervisor}.
 */
public class DefaultExecutionSupervisor implements ExecutionSupervisor {

    private final int maxRetries;

    public DefaultExecutionSupervisor(int maxRetries) {
        this.maxRetries = maxRetries > 0 ? maxRetries : 3;
    }

    public DefaultExecutionSupervisor() {
        this(3);
    }

    @Override
    public SupervisionDecision supervise(SupervisionContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        if (context.state() == ExecutionLifecycleState.FAILED) {
            if (context.consecutiveFailures() < maxRetries) {
                return SupervisionDecision.retry(
                        "Automatic recovery attempt #" + (context.consecutiveFailures() + 1),
                        Duration.ofSeconds((long) Math.pow(2, context.consecutiveFailures()))
                );
            } else {
                return SupervisionDecision.of(ControlAction.QUARANTINE, "Exceeded maximum retry limit (" + maxRetries + ")");
            }
        }

        if (context.state() == ExecutionLifecycleState.RUNNING) {
            return SupervisionDecision.continueExecution();
        }

        if (context.state().isTerminal()) {
            return SupervisionDecision.of(ControlAction.CONTINUE, "Execution is in terminal state: " + context.state());
        }

        return SupervisionDecision.continueExecution();
    }
}
