package tech.kayys.andalus.harness.fabric;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.FencedLeaseManager;
import tech.kayys.andalus.execution.mobility.FencingToken;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionLeaseObservabilityTest {
    @Test
    void emitsRenewalAndOwnershipEventsWithoutChangingLeaseBehavior() throws Exception {
        FencingToken token = FencingToken.of(71);
        CountDownLatch renewed = new CountDownLatch(1);
        AtomicInteger commits = new AtomicInteger();
        List<ExecutionLeaseObservation> observations = new CopyOnWriteArrayList<>();
        FencedLeaseManager leases = new FencedLeaseManager() {
            @Override public ExecutionLease acquireLease(String id, String holder, Duration duration) {
                return new ExecutionLease(id, holder, token, Instant.now(), Instant.now().plus(duration));
            }
            @Override public boolean renewLease(String id, String holder, FencingToken fence, Duration duration) {
                renewed.countDown();
                return true;
            }
            @Override public boolean releaseLease(String id, String holder, FencingToken fence) { return true; }
            @Override public boolean renewLease(String id, String holder, Duration duration) { fail("fenced renewal expected"); return false; }
            @Override public void releaseLease(String id, String holder) { fail("fenced release expected"); }
            @Override public boolean validateToken(String id, FencingToken fence) { return true; }
            @Override public Optional<FencingToken> currentToken(String id) { return Optional.of(token); }
            @Override public Optional<ExecutionLease> getLease(String id) { return Optional.empty(); }
        };
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            ExecutionLeaseBoundTaskExecutionHandler handler = new ExecutionLeaseBoundTaskExecutionHandler(
                    leases, Duration.ofSeconds(2), scheduler, Duration.ofMillis(10),
                    (task, visibility, fence) -> {
                        assertTrue(renewed.await(1, TimeUnit.SECONDS));
                    },
                    (assignment, claimToken, fence) -> commits.incrementAndGet(),
                    observations::add);

            handler.execute(testClaim(), extension -> true);

            assertEquals(1, commits.get());
            assertTrue(observations.stream().anyMatch(o -> o.type() == ExecutionLeaseObservation.Type.ACQUIRED));
            assertTrue(observations.stream().anyMatch(o ->
                    o.type() == ExecutionLeaseObservation.Type.RENEWAL_SUCCEEDED
                            && o.renewalDuration() != null && !o.renewalDuration().isNegative()));
            assertTrue(observations.stream().anyMatch(o -> o.type() == ExecutionLeaseObservation.Type.RELEASED));
        } finally {
            scheduler.shutdownNow();
        }
    }

    @Test
    void observerFailureDoesNotPreventCommitOrRelease() throws Exception {
        AtomicInteger commits = new AtomicInteger();
        AtomicInteger releases = new AtomicInteger();
        var manager = new tech.kayys.andalus.execution.core.mobility.DefaultLeaseManager();
        ExecutionLeaseBoundTaskExecutionHandler handler = new ExecutionLeaseBoundTaskExecutionHandler(
                manager, Duration.ofMinutes(1),
                (task, visibility, fence) -> { },
                (assignment, claimToken, fence) -> commits.incrementAndGet(),
                observation -> { throw new IllegalStateException("observer unavailable"); });

        handler.execute(testClaim(), extension -> true);

        assertEquals(1, commits.get());
        assertTrue(manager.getLease("lease-observability").isEmpty());
    }

    private TaskClaim testClaim() {
        AssignmentId id = AssignmentId.of("lease-observability-assignment");
        DefaultExecutionAssignment assignment = new DefaultExecutionAssignment(
                id, tech.kayys.andalus.harness.consistency.state.ExecutionId.of("lease-observability"),
                tech.kayys.andalus.workflow.graph.NodeId.of("node"),
                AttemptId.initial(), WorkerId.of("scheduler"), AssignmentFence.of(id, 1),
                AssignmentStatus.PENDING, AssignmentConstraints.defaults());
        Instant now = Instant.now();
        return new TaskClaim(assignment,
                new TaskClaimToken(id, WorkerId.of("lease-observability-worker"), 1),
                now, now.plusSeconds(30));
    }
}
