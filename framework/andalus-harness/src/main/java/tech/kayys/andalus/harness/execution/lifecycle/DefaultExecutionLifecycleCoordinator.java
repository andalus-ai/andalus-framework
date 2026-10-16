package tech.kayys.andalus.harness.execution.lifecycle;

import tech.kayys.andalus.harness.execution.state.DefaultExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionStateStore;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;
import tech.kayys.andalus.harness.lifecycle.HarnessExecutionStatus;
import tech.kayys.andalus.harness.lifecycle.HarnessLifecycle;

import java.util.Objects;

/**
 * Default implementation of {@link ExecutionLifecycleCoordinator} (6.15.3.4).
 *
 * <p><strong>Ordering guarantee:</strong> durable state is persisted <em>before</em> the
 * in-process lifecycle is updated. A process crash after persistence but before the lifecycle
 * update leaves a recoverable durable record — the lifecycle is reconstructed from durable
 * state on restart via {@link ExecutionStatusMapper#harness}.
 *
 * <p><strong>Concurrency:</strong> {@link #transitionTo} is {@code synchronized} to prevent
 * two concurrent threads from racing to transition the same execution. The
 * {@link ExecutionStateStore#save} optimistic-version check provides a second line of defence
 * for cross-process races.
 */
public final class DefaultExecutionLifecycleCoordinator implements ExecutionLifecycleCoordinator {

    private final ExecutionStateStore stateStore;
    private final HarnessLifecycle lifecycle;

    private volatile ExecutionState state;

    public DefaultExecutionLifecycleCoordinator(ExecutionState initialState,
                                                 ExecutionStateStore stateStore,
                                                 HarnessLifecycle lifecycle) {
        this.state      = Objects.requireNonNull(initialState, "initialState");
        this.stateStore = Objects.requireNonNull(stateStore,   "stateStore");
        this.lifecycle  = Objects.requireNonNull(lifecycle,    "lifecycle");
    }

    @Override
    public ExecutionState state() {
        return state;
    }

    @Override
    public HarnessExecutionStatus lifecycleStatus() {
        return lifecycle.status();
    }

    @Override
    public boolean isTerminal() {
        return state.status().isTerminal();
    }

    /**
     * Transitions both durable state and in-process lifecycle to the target status.
     *
     * <p>The transition is rejected if:
     * <ul>
     *   <li>The execution is already in a terminal state</li>
     *   <li>The lifecycle FSM does not permit the transition</li>
     * </ul>
     *
     * <p>The durable write happens first; any subsequent in-process lifecycle update failure
     * leaves a recoverable durable record.
     */
    @Override
    public synchronized void transitionTo(HarnessExecutionStatus target) {
        Objects.requireNonNull(target, "target");

        HarnessExecutionStatus current = lifecycle.status();
        if (current == target) return; // idempotent

        if (lifecycle.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot transition terminal execution from " + current + " to " + target);
        }

        // Map to durable status and guard against illegal durable transition
        ExecutionStatus durableTarget = ExecutionStatusMapper.durable(target);
        ExecutionState currentState   = state;

        if (!isDurableTransitionAllowed(currentState.status(), durableTarget)) {
            throw new IllegalStateException(
                    "Illegal durable transition: " + currentState.status() + " → " + durableTarget);
        }

        // 1. Persist durable state FIRST (authoritative)
        ExecutionState next = ((DefaultExecutionState) currentState).withStatus(durableTarget);
        stateStore.save(next, currentState.version());
        state = next;

        // 2. Update in-process lifecycle (projection)
        lifecycle.transitionTo(target);
    }

    // ── Legal durable transition table ───────────────────────────────────────

    private static boolean isDurableTransitionAllowed(ExecutionStatus from, ExecutionStatus to) {
        if (from == to) return true;
        if (from.isTerminal()) return false;
        return switch (from) {
            case CREATED    -> to == ExecutionStatus.RUNNING;
            case RUNNING    -> to == ExecutionStatus.WAITING
                            || to == ExecutionStatus.SUSPENDED
                            || to == ExecutionStatus.COMPLETED
                            || to == ExecutionStatus.FAILED
                            || to == ExecutionStatus.CANCELED
                            || to == ExecutionStatus.TIMEOUT;
            case WAITING    -> to == ExecutionStatus.RUNNING    // wake → RESUMING maps to RUNNING
                            || to == ExecutionStatus.CANCELED
                            || to == ExecutionStatus.FAILED
                            || to == ExecutionStatus.TIMEOUT;
            case SUSPENDED  -> to == ExecutionStatus.RUNNING
                            || to == ExecutionStatus.CANCELED
                            || to == ExecutionStatus.TIMEOUT;
            case RECOVERING -> to == ExecutionStatus.RUNNING
                            || to == ExecutionStatus.FAILED
                            || to == ExecutionStatus.CANCELED;
            default         -> false;
        };
    }
}
