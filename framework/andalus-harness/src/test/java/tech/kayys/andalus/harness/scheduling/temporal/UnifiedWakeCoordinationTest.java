package tech.kayys.andalus.harness.scheduling.temporal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.context.DefaultHarnessContext;
import tech.kayys.andalus.harness.context.DefaultHarnessIdentity;
import tech.kayys.andalus.harness.context.DefaultHarnessSession;
import tech.kayys.andalus.harness.execution.state.DefaultExecutionState;
import tech.kayys.andalus.harness.execution.state.ExecutionCursor;
import tech.kayys.andalus.harness.execution.state.ExecutionData;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.execution.state.ExecutionStatus;
import tech.kayys.andalus.harness.execution.state.InMemoryExecutionStateStore;
import tech.kayys.andalus.harness.journal.ExecutionJournal;
import tech.kayys.andalus.harness.journal.InMemoryExecutionJournal;
import tech.kayys.andalus.harness.observability.event.AndalusEvent;
import tech.kayys.andalus.harness.observability.event.DefaultAndalusEvent;
import tech.kayys.andalus.harness.observability.event.DefaultEventBus;
import tech.kayys.andalus.harness.observability.event.EventBus;
import tech.kayys.andalus.harness.observability.event.EventContext;
import tech.kayys.andalus.harness.observability.event.EventDomain;
import tech.kayys.andalus.harness.observability.event.EventFilter;
import tech.kayys.andalus.harness.observability.event.EventId;
import tech.kayys.andalus.harness.observability.event.EventPayload;
import tech.kayys.andalus.harness.observability.event.EventSequence;
import tech.kayys.andalus.harness.observability.event.EventType;
import tech.kayys.andalus.harness.scheduling.clock.AndalusClock;
import tech.kayys.andalus.harness.scheduling.scheduler.DefaultScheduler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Unified Wake Coordination (6.15.5):
 * - EventWakeRegistration
 * - ExecutionWakeRegistration (multi-waiter, self-wait prevention)
 * - DefaultWakeCoordinator
 * - DefaultDurableWaitController (durable-first ordering)
 */
class UnifiedWakeCoordinationTest {

    private EventBus eventBus;
    private StubWaitController waitController;
    private EventWakeRegistration eventWake;
    private ExecutionWakeRegistration execWake;
    private DefaultScheduler scheduler;
    private DefaultWakeCoordinator wakeCoordinator;
    private InMemoryExecutionStateStore stateStore;
    private ExecutionJournal journal;
    private DefaultDurableWaitCoordinator durableCoordinator;
    private DefaultDurableWaitController waitControllerImpl;

    @BeforeEach
    void setUp() {
        eventBus = new DefaultEventBus();
        waitController = new StubWaitController();
        eventWake = new EventWakeRegistration(eventBus, waitController);
        execWake = new ExecutionWakeRegistration(waitController);
        scheduler = new DefaultScheduler(AndalusClock.system());
        wakeCoordinator = new DefaultWakeCoordinator(scheduler, eventWake, execWake);

        stateStore = new InMemoryExecutionStateStore();
        journal = new InMemoryExecutionJournal();
        durableCoordinator = new DefaultDurableWaitCoordinator(stateStore, journal);
        waitControllerImpl = new DefaultDurableWaitController(durableCoordinator, wakeCoordinator);
    }

    @Test
    void eventWakeRegistration_eventArrival_wakesExecution() {
        ExecutionId execId = ExecutionId.of("exec-event-1");
        EventFilter filter = EventFilter.forExecution(execId);

        eventWake.register(execId, new WakeCondition.EventWake(filter));

        // Matching event arrives
        AndalusEvent event = createEvent(execId, "ORDER_PAID");
        eventBus.publish(event);

        assertEquals(1, waitController.woken.size());
        assertEquals(execId, waitController.woken.get(0));

        // Unsubscribe via cancel
        eventWake.cancel(execId);
        eventBus.publish(event);
        assertEquals(1, waitController.woken.size(), "Cancelled subscription must not wake again");
    }

    @Test
    void executionWakeRegistration_targetCompleted_wakesAllWaiters() {
        ExecutionId target = ExecutionId.of("exec-target-1");
        ExecutionId waiter1 = ExecutionId.of("exec-waiter-1");
        ExecutionId waiter2 = ExecutionId.of("exec-waiter-2");

        execWake.register(waiter1, target);
        execWake.register(waiter2, target);
        assertEquals(1, execWake.activeTargetCount());

        execWake.completed(target);

        assertEquals(2, waitController.woken.size());
        assertTrue(waitController.woken.contains(waiter1));
        assertTrue(waitController.woken.contains(waiter2));
        assertEquals(0, execWake.activeTargetCount());
    }

    @Test
    void executionWakeRegistration_selfWait_isRejected() {
        ExecutionId same = ExecutionId.of("exec-self");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> execWake.register(same, same));
        assertTrue(ex.getMessage().contains("Execution cannot wait for itself"));
    }

    @Test
    void defaultWakeCoordinator_registerAndCancelTimeWake() {
        ExecutionId execId = ExecutionId.of("exec-time-1");
        WakeCondition.TimeWake timeWake = new WakeCondition.TimeWake(Instant.now().plusSeconds(60));

        wakeCoordinator.register(execId, timeWake);
        assertTrue(wakeCoordinator.hasActiveSchedule(execId));

        wakeCoordinator.cancel(execId);
        assertFalse(wakeCoordinator.hasActiveSchedule(execId));
    }

    @Test
    void durableWaitController_waitFor_persistsDurableWaitingFirst() {
        ExecutionId execId = ExecutionId.of("exec-ctrl-1");
        saveState(execId, ExecutionStatus.RUNNING);

        WakeCondition.EventWake condition = new WakeCondition.EventWake(EventFilter.forExecution(execId));
        waitControllerImpl.waitFor(execId, condition);

        // Verify durable state was saved to WAITING
        Optional<tech.kayys.andalus.harness.execution.state.ExecutionState> stateOpt = stateStore.load(execId);
        assertTrue(stateOpt.isPresent());
        assertEquals(ExecutionStatus.WAITING, stateOpt.get().status());

        // Wake it
        waitControllerImpl.wake(execId);
        assertEquals(ExecutionStatus.RUNNING, stateStore.load(execId).get().status());
    }

    private void saveState(ExecutionId id, ExecutionStatus status) {
        var ctx = new DefaultHarnessContext(
                DefaultHarnessIdentity.of("agent-1"),
                DefaultHarnessSession.createNew(),
                Map.of()
        );
        stateStore.save(new DefaultExecutionState(id, status, 0L, ctx, ExecutionCursor.initial(), ExecutionData.empty()), -1L);
    }

    private AndalusEvent createEvent(ExecutionId executionId, String action) {
        return DefaultAndalusEvent.of(
                EventType.of(EventDomain.EXECUTION, action),
                EventContext.ofExecution(executionId),
                EventPayload.empty()
        );
    }

    private static class StubWaitController implements DurableWaitController {
        final List<ExecutionId> woken = new ArrayList<>();

        @Override
        public void waitFor(ExecutionId executionId, WakeCondition condition) {}

        @Override
        public void wake(ExecutionId executionId) {
            woken.add(executionId);
        }

        @Override
        public void cancelWake(ExecutionId executionId) {}
    }
}
