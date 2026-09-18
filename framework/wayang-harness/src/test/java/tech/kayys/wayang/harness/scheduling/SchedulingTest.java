package tech.kayys.wayang.harness.scheduling;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.scheduling.clock.VirtualClock;
import tech.kayys.wayang.harness.scheduling.retry.BackoffStrategy;
import tech.kayys.wayang.harness.scheduling.retry.RetryDecision;
import tech.kayys.wayang.harness.scheduling.retry.RetryPolicy;
import tech.kayys.wayang.harness.scheduling.scheduler.DefaultScheduler;
import tech.kayys.wayang.harness.scheduling.scheduler.ScheduleHandle;
import tech.kayys.wayang.harness.scheduling.scheduler.ScheduleRequest;
import tech.kayys.wayang.harness.scheduling.scheduler.ScheduleSpec;
import tech.kayys.wayang.harness.scheduling.scheduler.ScheduleState;
import tech.kayys.wayang.harness.scheduling.temporal.Deadline;
import tech.kayys.wayang.harness.scheduling.temporal.WakeCondition;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SchedulingTest {

    @Test
    void testVirtualClockAndDurableScheduler() {
        VirtualClock clock = VirtualClock.atEpoch();
        DefaultScheduler scheduler = new DefaultScheduler(clock);
        ExecutionId execId = ExecutionId.of("exec-sched-1");

        ScheduleRequest req = ScheduleRequest.of(execId, ScheduleSpec.after(Duration.ofMinutes(10), clock.now()));
        ScheduleHandle handle = scheduler.schedule(req);

        assertEquals(ScheduleState.SCHEDULED, handle.state());
        assertTrue(scheduler.findDue().isEmpty());

        // Advance virtual clock by 11 minutes
        clock.advance(Duration.ofMinutes(11));
        Collection<ScheduleHandle> due = scheduler.findDue();
        assertEquals(1, due.size());
        assertEquals(handle.id(), due.iterator().next().id());
    }

    @Test
    void testRetryPolicyWithBackoff() {
        BackoffStrategy backoff = BackoffStrategy.exponential(Duration.ofSeconds(1), 2.0, Duration.ofSeconds(10));
        RetryPolicy policy = RetryPolicy.maxAttempts(3, backoff);

        RetryDecision d1 = policy.evaluate(new RuntimeException("transient"), 0);
        assertTrue(d1.shouldRetry());
        assertEquals(Duration.ofSeconds(1), d1.delay());

        RetryDecision d2 = policy.evaluate(new RuntimeException("transient"), 1);
        assertTrue(d2.shouldRetry());
        assertEquals(Duration.ofSeconds(2), d2.delay());

        RetryDecision d3 = policy.evaluate(new RuntimeException("transient"), 3);
        assertFalse(d3.shouldRetry());
    }

    @Test
    void testDeadlineAndWakeCondition() {
        Instant now = Instant.now();
        Deadline deadline = new Deadline(now.plusSeconds(60));
        assertFalse(deadline.isExceeded(now));
        assertTrue(deadline.isExceeded(now.plusSeconds(61)));

        WakeCondition.TimeWake timeWake = new WakeCondition.TimeWake(now.plusSeconds(300));
        assertEquals(now.plusSeconds(300), timeWake.wakeAt());

        WakeCondition.HumanWake humanWake = new WakeCondition.HumanWake("appr-123");
        assertEquals("appr-123", humanWake.approvalId());
    }
}
