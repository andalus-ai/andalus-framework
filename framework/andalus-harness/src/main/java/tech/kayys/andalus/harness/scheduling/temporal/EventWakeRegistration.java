package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.observability.event.AndalusEvent;
import tech.kayys.andalus.harness.observability.event.EventBus;
import tech.kayys.andalus.harness.observability.event.EventSubscription;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages transient EventBus subscriptions for EventWake conditions (6.15.5.5).
 *
 * <p>Event delivery is only a wake signal. The durable wait controller
 * reloads ExecutionState to verify the execution is still WAITING before resuming.</p>
 */
public final class EventWakeRegistration {

    private final EventBus eventBus;
    private final DurableWaitController waitController;

    private final Map<ExecutionId, EventSubscription> subscriptions =
            new ConcurrentHashMap<>();

    public EventWakeRegistration(EventBus eventBus, DurableWaitController waitController) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
        this.waitController = Objects.requireNonNull(waitController, "waitController");
    }

    public void register(
            ExecutionId executionId,
            WakeCondition.EventWake wake
    ) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(wake, "wake");

        cancel(executionId);

        EventSubscription subscription =
                eventBus.subscribe(
                        wake.filter(),
                        event -> onEvent(executionId, event)
                );

        subscriptions.put(executionId, subscription);
    }

    public void cancel(ExecutionId executionId) {
        EventSubscription subscription = subscriptions.remove(executionId);

        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void onEvent(
            ExecutionId executionId,
            AndalusEvent event
    ) {
        /*
         * Event delivery is only a wake signal.
         * The durable wait controller reloads ExecutionState to check if still WAITING.
         */
        waitController.wake(executionId);
    }
}
