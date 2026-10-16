package tech.kayys.andalus.harness.scheduling.temporal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.scheduling.clock.AndalusClock;
import tech.kayys.andalus.harness.scheduling.scheduler.DefaultScheduler;
import tech.kayys.andalus.harness.scheduling.scheduler.ScheduleHandle;
import tech.kayys.andalus.harness.scheduling.scheduler.ScheduleRequest;
import tech.kayys.andalus.harness.scheduling.scheduler.ScheduleSpec;
import tech.kayys.andalus.harness.scheduling.scheduler.ScheduleState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TimeWakeDispatcher and DefaultScheduler.claimDue() (6.15.4).
 */
class TimeWakeDispatcherTest {

    private TestClock clock;
    private DefaultScheduler scheduler;
    private StubWaitController waitController;
    private TimeWakeDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        clock = new TestClock(Instant.parse("2026-09-21T10:00:00Z"));
        scheduler = new DefaultScheduler(clock);
        waitController = new StubWaitController();
        dispatcher = new TimeWakeDispatcher(scheduler, waitController);
    }

    @Test
    void claimDue_atomicallyClaimsScheduled_transitionsToFired() {
        ExecutionId execId = ExecutionId.of("exec-1");
        Instant trigger = clock.now().plusSeconds(10);
        ScheduleHandle handle = scheduler.schedule(ScheduleRequest.of(execId, ScheduleSpec.at(trigger)));
        assertEquals(ScheduleState.SCHEDULED, handle.state());

        // Before due time
        assertTrue(scheduler.claimDue().isEmpty(), "Not due yet -> no claim");

        // Advance clock past trigger
        clock.set(trigger.plusSeconds(1));

        // First claim
        Collection<ScheduleHandle> claimed = scheduler.claimDue();
        assertEquals(1, claimed.size());
        assertEquals(ScheduleState.FIRED, claimed.iterator().next().state());

        // Second claim: should NOT return anything because it was already claimed to FIRED (6.15.4.4)
        assertTrue(scheduler.claimDue().isEmpty(), "Already claimed schedule must not be returned again");
    }

    @Test
    void dispatchDue_wakesExecution() {
        ExecutionId execId = ExecutionId.of("exec-2");
        scheduler.schedule(ScheduleRequest.of(execId, ScheduleSpec.at(clock.now())));

        dispatcher.dispatchDue();

        assertEquals(1, waitController.woken.size());
        assertEquals(execId, waitController.woken.get(0));

        // Duplicate polling -> no second dispatch
        dispatcher.dispatchDue();
        assertEquals(1, waitController.woken.size());
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

        void set(Instant newNow) {
            this.now = newNow;
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
}
