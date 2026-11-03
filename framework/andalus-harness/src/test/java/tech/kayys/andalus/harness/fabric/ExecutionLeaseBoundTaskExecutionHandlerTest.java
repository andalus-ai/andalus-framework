package tech.kayys.andalus.harness.fabric;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.FencingToken;
import tech.kayys.andalus.execution.mobility.LeaseManager;
import tech.kayys.andalus.execution.mobility.FencedLeaseManager;
import tech.kayys.andalus.execution.core.mobility.DefaultLeaseManager;
import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.workflow.graph.NodeId;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionLeaseBoundTaskExecutionHandlerTest {
    private TaskClaim claim() {
        AssignmentId id = AssignmentId.of("lease-bound");
        DefaultExecutionAssignment assignment = new DefaultExecutionAssignment(
                id, ExecutionId.of("exec-lease-bound"), NodeId.of("node"),
                AttemptId.initial(), WorkerId.of("scheduler"),
                AssignmentFence.of(id, 3), AssignmentStatus.PENDING,
                AssignmentConstraints.defaults());
        Instant now = Instant.now();
        return new TaskClaim(assignment,
                new TaskClaimToken(id, WorkerId.of("worker-a"), 9),
                now, now.plusSeconds(30));
    }

    private LeaseManager leaseManager(AtomicBoolean valid, AtomicInteger releases) {
        FencingToken token = FencingToken.of(41);
        return new LeaseManager() {
            @Override public ExecutionLease acquireLease(String executionId, String holderId, Duration duration) {
                assertEquals("exec-lease-bound", executionId);
                assertEquals("worker-a", holderId);
                return new ExecutionLease(executionId, holderId, token, Instant.now(), Instant.now().plus(duration));
            }
            @Override public boolean renewLease(String executionId, String holderId, Duration duration) { return valid.get(); }
            @Override public void releaseLease(String executionId, String holderId) { releases.incrementAndGet(); valid.set(false); }
            @Override public boolean validateToken(String executionId, FencingToken supplied) {
                return valid.get() && token.equals(supplied);
            }
            @Override public Optional<FencingToken> currentToken(String executionId) { return Optional.of(token); }
            @Override public Optional<ExecutionLease> getLease(String executionId) { return Optional.empty(); }
        };
    }

    @Test
    void acquiresExecutionFenceBeforeWorkAndCommitsWithSameToken() throws Exception {
        AtomicBoolean valid = new AtomicBoolean(true);
        AtomicInteger releases = new AtomicInteger();
        FencingToken expected = FencingToken.of(41);
        AtomicInteger stages = new AtomicInteger();
        ExecutionLeaseBoundTaskExecutionHandler handler = new ExecutionLeaseBoundTaskExecutionHandler(
                leaseManager(valid, releases), Duration.ofMinutes(1),
                (task, visibility, fence) -> {
                    assertEquals(expected, fence);
                    assertEquals(0, stages.getAndIncrement());
                },
                (assignment, claimToken, fence) -> {
                    assertEquals(expected, fence);
                    assertEquals(1, stages.getAndIncrement());
                    assertEquals(9, claimToken.generation());
                });

        handler.execute(claim(), extension -> true);

        assertEquals(2, stages.get());
        assertEquals(1, releases.get());
    }

    @Test
    void lostExecutionLeasePreventsCompletionCommit() {
        AtomicBoolean valid = new AtomicBoolean(false);
        AtomicInteger releases = new AtomicInteger();
        AtomicInteger commits = new AtomicInteger();
        ExecutionLeaseBoundTaskExecutionHandler handler = new ExecutionLeaseBoundTaskExecutionHandler(
                leaseManager(valid, releases), Duration.ofMinutes(1),
                (task, visibility, fence) -> fail("work must not start without a valid lease"),
                (assignment, claimToken, fence) -> commits.incrementAndGet());

        assertThrows(StaleExecutionCompletionException.class,
                () -> handler.execute(claim(), extension -> true));
        assertEquals(0, commits.get());
        assertEquals(1, releases.get());
    }

    @Test
    void staleFinallyCannotReleaseSuccessorLeaseOwnedBySameHolder() {
        DefaultLeaseManager leases = new DefaultLeaseManager();
        AtomicBoolean successorAcquired = new AtomicBoolean();
        ExecutionLeaseBoundTaskExecutionHandler handler = new ExecutionLeaseBoundTaskExecutionHandler(
                leases, Duration.ofMinutes(1),
                (task, visibility, fence) -> {
                    leases.releaseLease("exec-lease-bound", "worker-a");
                    ExecutionLease successor = leases.acquireLease(
                            "exec-lease-bound", "worker-a", Duration.ofMinutes(1));
                    assertTrue(successor.fencingToken().generation() > fence.generation());
                    successorAcquired.set(true);
                },
                (assignment, token, fence) -> fail("stale work must not commit"));

        assertThrows(StaleExecutionCompletionException.class,
                () -> handler.execute(claim(), extension -> true));

        assertTrue(successorAcquired.get());
        assertTrue(leases.getLease("exec-lease-bound").isPresent(),
                "cleanup for the old generation must not release the successor lease");
    }

    @Test
    void heartbeatRenewsWithExactFenceAndLostRenewalBlocksCompletion() throws Exception {
        FencingToken token = FencingToken.of(52);
        CountDownLatch renewalAttempted = new CountDownLatch(1);
        AtomicInteger commits = new AtomicInteger();
        AtomicInteger releases = new AtomicInteger();
        FencedLeaseManager leases = new FencedLeaseManager() {
            @Override public ExecutionLease acquireLease(String executionId, String holderId, Duration duration) {
                return new ExecutionLease(executionId, holderId, token, Instant.now(), Instant.now().plus(duration));
            }
            @Override public boolean renewLease(String executionId, String holderId, FencingToken supplied, Duration duration) {
                assertEquals(token, supplied);
                renewalAttempted.countDown();
                return false;
            }
            @Override public boolean releaseLease(String executionId, String holderId, FencingToken supplied) {
                assertEquals(token, supplied);
                releases.incrementAndGet();
                return true;
            }
            @Override public boolean renewLease(String executionId, String holderId, Duration duration) {
                fail("fenced renewal must be used");
                return false;
            }
            @Override public void releaseLease(String executionId, String holderId) {
                fail("fenced release must be used");
            }
            @Override public boolean validateToken(String executionId, FencingToken supplied) {
                return token.equals(supplied);
            }
            @Override public Optional<FencingToken> currentToken(String executionId) { return Optional.of(token); }
            @Override public Optional<ExecutionLease> getLease(String executionId) { return Optional.empty(); }
        };
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            ExecutionLeaseBoundTaskExecutionHandler handler = new ExecutionLeaseBoundTaskExecutionHandler(
                    leases, Duration.ofSeconds(2), scheduler, Duration.ofMillis(10),
                    (task, visibility, fence) -> {
                        assertEquals(token, fence);
                        assertTrue(renewalAttempted.await(1, TimeUnit.SECONDS));
                    },
                    (assignment, claimToken, fence) -> commits.incrementAndGet());

            assertThrows(StaleExecutionCompletionException.class,
                    () -> handler.execute(claim(), extension -> true));
            assertEquals(0, commits.get());
            assertEquals(1, releases.get());
        } finally {
            scheduler.shutdownNow();
        }
    }

    @Test
    void rejectsHeartbeatIntervalNotShorterThanLease() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            assertThrows(IllegalArgumentException.class, () ->
                    new ExecutionLeaseBoundTaskExecutionHandler(
                            leaseManager(new AtomicBoolean(true), new AtomicInteger()),
                            Duration.ofSeconds(1), scheduler, Duration.ofSeconds(1),
                            (task, visibility, fence) -> {},
                            (assignment, token, fence) -> {}));
        } finally {
            scheduler.shutdownNow();
        }
    }

    @Test
    void rejectsNonPositiveLeaseDuration() {
        assertThrows(IllegalArgumentException.class, () ->
                new ExecutionLeaseBoundTaskExecutionHandler(
                        leaseManager(new AtomicBoolean(true), new AtomicInteger()),
                        Duration.ZERO, (task, visibility, fence) -> {},
                        (assignment, token, fence) -> {}));
    }
}
