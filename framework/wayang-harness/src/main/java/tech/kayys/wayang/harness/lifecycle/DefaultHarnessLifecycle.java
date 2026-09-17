package tech.kayys.wayang.harness.lifecycle;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe finite state machine enforcing legal Harness lifecycle transitions.
 */
public class DefaultHarnessLifecycle implements HarnessLifecycle {

    private static final Map<HarnessExecutionStatus, Set<HarnessExecutionStatus>> LEGAL_TRANSITIONS = new EnumMap<>(HarnessExecutionStatus.class);

    static {
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.CREATED, EnumSet.of(
                HarnessExecutionStatus.ADMITTED,
                HarnessExecutionStatus.CANCELLED,
                HarnessExecutionStatus.FAILED
        ));
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.ADMITTED, EnumSet.of(
                HarnessExecutionStatus.INITIALIZING,
                HarnessExecutionStatus.CANCELLED,
                HarnessExecutionStatus.FAILED
        ));
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.INITIALIZING, EnumSet.of(
                HarnessExecutionStatus.RUNNING,
                HarnessExecutionStatus.CANCELLED,
                HarnessExecutionStatus.FAILED
        ));
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.RUNNING, EnumSet.of(
                HarnessExecutionStatus.WAITING,
                HarnessExecutionStatus.SUSPENDED,
                HarnessExecutionStatus.COMPLETED,
                HarnessExecutionStatus.FAILED,
                HarnessExecutionStatus.CANCELLED,
                HarnessExecutionStatus.EXPIRED
        ));
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.WAITING, EnumSet.of(
                HarnessExecutionStatus.RESUMING,
                HarnessExecutionStatus.RUNNING,
                HarnessExecutionStatus.CANCELLED,
                HarnessExecutionStatus.FAILED,
                HarnessExecutionStatus.EXPIRED
        ));
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.SUSPENDED, EnumSet.of(
                HarnessExecutionStatus.RESUMING,
                HarnessExecutionStatus.CANCELLED,
                HarnessExecutionStatus.EXPIRED
        ));
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.RESUMING, EnumSet.of(
                HarnessExecutionStatus.RUNNING,
                HarnessExecutionStatus.CANCELLED,
                HarnessExecutionStatus.FAILED
        ));
        // Terminal states cannot transition to any other state
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.COMPLETED, EnumSet.noneOf(HarnessExecutionStatus.class));
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.FAILED, EnumSet.noneOf(HarnessExecutionStatus.class));
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.CANCELLED, EnumSet.noneOf(HarnessExecutionStatus.class));
        LEGAL_TRANSITIONS.put(HarnessExecutionStatus.EXPIRED, EnumSet.noneOf(HarnessExecutionStatus.class));
    }

    private final AtomicReference<HarnessExecutionStatus> currentStatus;

    public DefaultHarnessLifecycle() {
        this(HarnessExecutionStatus.CREATED);
    }

    public DefaultHarnessLifecycle(HarnessExecutionStatus initialStatus) {
        this.currentStatus = new AtomicReference<>(Objects.requireNonNull(initialStatus, "initialStatus"));
    }

    @Override
    public HarnessExecutionStatus status() {
        return currentStatus.get();
    }

    @Override
    public void transitionTo(HarnessExecutionStatus target) {
        Objects.requireNonNull(target, "target status");
        currentStatus.updateAndGet(current -> {
            if (current == target) {
                return current;
            }
            Set<HarnessExecutionStatus> legal = LEGAL_TRANSITIONS.getOrDefault(current, Set.of());
            if (!legal.contains(target)) {
                throw new IllegalStateException(String.format(
                        "Illegal lifecycle transition: cannot transition from %s to %s",
                        current, target
                ));
            }
            return target;
        });
    }

    @Override
    public boolean isTerminal() {
        return currentStatus.get().isTerminal();
    }

    @Override
    public void cancel() {
        if (!isTerminal()) {
            transitionTo(HarnessExecutionStatus.CANCELLED);
        }
    }

    @Override
    public void suspend() {
        transitionTo(HarnessExecutionStatus.SUSPENDED);
    }

    @Override
    public void resume() {
        transitionTo(HarnessExecutionStatus.RESUMING);
    }
}
