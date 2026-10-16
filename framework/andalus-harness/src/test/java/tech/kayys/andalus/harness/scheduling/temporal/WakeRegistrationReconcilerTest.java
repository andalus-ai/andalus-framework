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
import tech.kayys.andalus.harness.observability.event.EventFilter;
import tech.kayys.andalus.harness.scheduling.clock.AndalusClock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DefaultWakeRegistrationReconciler and ExecutionStateDiscovery (6.15.6).
 */
class WakeRegistrationReconcilerTest {

    private InMemoryExecutionStateStore stateStore;
    private StubWaitController waitController;
    private StubWakeRegistration wakeRegistration;
    private TestClock clock;
    private Map<ExecutionId, WakeCondition> conditions;
    private DefaultWakeRegistrationReconciler reconciler;

    @BeforeEach
    void setUp() {
        stateStore = new InMemoryExecutionStateStore();
        waitController = new StubWaitController();
        wakeRegistration = new StubWakeRegistration();
        clock = new TestClock(Instant.parse("2026-09-21T12:00:00Z"));
        conditions = new HashMap<>();

        reconciler = new DefaultWakeRegistrationReconciler(
                stateStore,
                waitController,
                wakeRegistration,
                clock,
                id -> Optional.ofNullable(conditions.get(id))
        );
    }

    @Test
    void executionStateDiscovery_findsWaitingExecutions() {
        ExecutionId waiting1 = ExecutionId.of("wait-1");
        ExecutionId waiting2 = ExecutionId.of("wait-2");
        ExecutionId running = ExecutionId.of("run-1");
        ExecutionId completed = ExecutionId.of("comp-1");

        saveState(waiting1, ExecutionStatus.WAITING);
        saveState(waiting2, ExecutionStatus.WAITING);
        saveState(running, ExecutionStatus.RUNNING);
        saveState(completed, ExecutionStatus.COMPLETED);

        Collection<ExecutionId> waitingList = stateStore.findWaitingExecutions();
        assertEquals(2, waitingList.size());
        assertTrue(waitingList.contains(waiting1));
        assertTrue(waitingList.contains(waiting2));
    }

    @Test
    void reconcile_skipsNonWaitingOrMissing() {
        ExecutionId running = ExecutionId.of("run-1");
        saveState(running, ExecutionStatus.RUNNING);
        conditions.put(running, new WakeCondition.HumanWake("app-1"));

        reconciler.reconcile(running);
        assertTrue(waitController.woken.isEmpty());
        assertTrue(wakeRegistration.registered.isEmpty());

        ExecutionId missing = ExecutionId.of("missing");
        reconciler.reconcile(missing);
        assertTrue(waitController.woken.isEmpty());
        assertTrue(wakeRegistration.registered.isEmpty());
    }

    @Test
    void reconcile_timeWake_pastWakeTime_wakesImmediately() {
        ExecutionId execId = ExecutionId.of("time-past");
        saveState(execId, ExecutionStatus.WAITING);
        // Due 10 minutes ago
        Instant wakeAt = clock.now().minusSeconds(600);
        conditions.put(execId, new WakeCondition.TimeWake(wakeAt));

        reconciler.reconcile(execId);

        assertEquals(1, waitController.woken.size(), "Missed timer must wake immediately (6.15.6.6)");
        assertEquals(execId, waitController.woken.get(0));
        assertTrue(wakeRegistration.registered.isEmpty(), "Must not schedule in the past");
    }

    @Test
    void reconcile_timeWake_futureWakeTime_schedules() {
        ExecutionId execId = ExecutionId.of("time-future");
        saveState(execId, ExecutionStatus.WAITING);
        // Due 10 minutes in the future
        Instant wakeAt = clock.now().plusSeconds(600);
        conditions.put(execId, new WakeCondition.TimeWake(wakeAt));

        reconciler.reconcile(execId);

        assertTrue(waitController.woken.isEmpty());
        assertEquals(1, wakeRegistration.registered.size());
        assertEquals(execId, wakeRegistration.registered.get(0));
    }

    @Test
    void reconcile_eventWake_resubscribes() {
        ExecutionId execId = ExecutionId.of("event-reconcile");
        saveState(execId, ExecutionStatus.WAITING);
        conditions.put(execId, new WakeCondition.EventWake(EventFilter.all()));

        reconciler.reconcile(execId);

        assertTrue(waitController.woken.isEmpty());
        assertEquals(1, wakeRegistration.registered.size());
        assertEquals(execId, wakeRegistration.registered.get(0));
    }

    @Test
    void reconcile_executionWake_targetAlreadyTerminal_wakesImmediately() {
        ExecutionId target = ExecutionId.of("target-done");
        ExecutionId waiter = ExecutionId.of("waiter-1");

        saveState(target, ExecutionStatus.COMPLETED);
        saveState(waiter, ExecutionStatus.WAITING);
        conditions.put(waiter, new WakeCondition.ExecutionWake(target));

        reconciler.reconcile(waiter);

        assertEquals(1, waitController.woken.size(), "Target already terminal -> wake immediately (6.15.6.10)");
        assertEquals(waiter, waitController.woken.get(0));
    }

    @Test
    void reconcile_executionWake_targetStillRunning_registersListener() {
        ExecutionId target = ExecutionId.of("target-running");
        ExecutionId waiter = ExecutionId.of("waiter-2");

        saveState(target, ExecutionStatus.RUNNING);
        saveState(waiter, ExecutionStatus.WAITING);
        conditions.put(waiter, new WakeCondition.ExecutionWake(target));

        reconciler.reconcile(waiter);

        assertTrue(waitController.woken.isEmpty());
        assertEquals(1, wakeRegistration.registered.size());
        assertEquals(waiter, wakeRegistration.registered.get(0));
    }

    private void saveState(ExecutionId id, ExecutionStatus status) {
        var ctx = new DefaultHarnessContext(
                DefaultHarnessIdentity.of("agent-1"),
                DefaultHarnessSession.createNew(),
                Map.of()
        );
        stateStore.save(new DefaultExecutionState(id, status, 0L, ctx, ExecutionCursor.initial(), ExecutionData.empty()), -1L);
    }

    private static class TestClock implements AndalusClock {
        private Instant now;

        TestClock(Instant initial) {
            this.now = initial;
        }

        @Override
        public Instant now() {
            return now;
        }
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

    private static class StubWakeRegistration implements WakeRegistration {
        final List<ExecutionId> registered = new ArrayList<>();

        @Override
        public void register(ExecutionId executionId, WakeCondition condition) {
            registered.add(executionId);
        }

        @Override
        public void cancel(ExecutionId executionId) {
            registered.remove(executionId);
        }
    }
}
