package tech.kayys.andalus.harness.fabric;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.workflow.graph.NodeId;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class WorkerTaskPollerTest {
    private DefaultExecutionAssignment assignment(String id) {
        AssignmentId assignmentId = AssignmentId.of(id);
        return new DefaultExecutionAssignment(assignmentId, ExecutionId.of("exec-" + id),
                NodeId.of("node-" + id), AttemptId.initial(), WorkerId.of("assigned-worker"),
                AssignmentFence.of(assignmentId, 1), AssignmentStatus.PENDING,
                AssignmentConstraints.defaults());
    }

    @Test
    void acknowledgesOnlyAfterHandlerReturnsSuccessfullyAndHonorsBatchLimit() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("k1", assignment("a"));
        queue.enqueue("k2", assignment("b"));
        queue.enqueue("k3", assignment("c"));
        AtomicInteger executions = new AtomicInteger();
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"),
                (claim, visibility) -> executions.incrementAndGet(),
                (task, failure) -> Duration.ZERO, 2, Duration.ofSeconds(30));

        TaskPollResult first = poller.pollOnce();
        assertEquals(2, first.claimed());
        assertEquals(2, first.completed());
        assertEquals(0, first.requeued());
        assertEquals(2, executions.get());
        TaskPollResult second = poller.pollOnce();
        assertEquals(1, second.claimed());
        assertEquals(1, second.completed());
    }

    @Test
    void failedHandlerNegativelyAcknowledgesForRedelivery() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("k1", assignment("a"));
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"),
                (claim, visibility) -> { throw new IllegalStateException("temporary"); },
                (task, failure) -> Duration.ZERO, 1, Duration.ofSeconds(30));

        TaskPollResult result = poller.pollOnce();
        assertEquals(1, result.claimed());
        assertEquals(1, result.requeued());
        assertEquals(0, result.completed());
        assertEquals(1, queue.claim(WorkerId.of("retry-worker"), 1, Duration.ofSeconds(30)).size());
    }

    @Test
    void lostClaimIsNotReportedAsCompletedWhenHandlerReturns() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("k1", assignment("a"));
        WorkerId worker = WorkerId.of("worker");
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, worker,
                (claim, visibility) -> assertTrue(queue.negativeAcknowledge(claim.token(), Duration.ZERO)),
                (task, failure) -> Duration.ZERO, 1, Duration.ofSeconds(30));

        TaskPollResult result = poller.pollOnce();
        assertEquals(1, result.lostClaims());
        assertEquals(0, result.completed());
    }

    @Test
    void handlerCanExtendItsClaimVisibility() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("k1", assignment("a"));
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"),
                (claim, visibility) -> assertTrue(visibility.extend(Duration.ofSeconds(30))),
                (task, failure) -> Duration.ZERO, 1, Duration.ofSeconds(5));

        TaskPollResult result = poller.pollOnce();
        assertEquals(1, result.completed());
        assertEquals(0, result.lostClaims());
    }

    @Test
    void validatesPollingConfiguration() {
        TaskQueue queue = new InMemoryTaskQueue();
        assertThrows(IllegalArgumentException.class, () -> new WorkerTaskPoller(queue,
                WorkerId.of("worker"), (claim, visibility) -> {}, (task, failure) -> Duration.ZERO,
                0, Duration.ofSeconds(1)));
        assertThrows(IllegalArgumentException.class, () -> new WorkerTaskPoller(queue,
                WorkerId.of("worker"), (claim, visibility) -> {}, (task, failure) -> Duration.ZERO,
                1, Duration.ZERO));
    }
}
