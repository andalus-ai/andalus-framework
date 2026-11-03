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

    private TaskFailureDecision retry(Exception failure) {
        return TaskFailureDecision.retry(Duration.ZERO, "TRANSIENT", failure.getMessage());
    }

    @Test
    void acknowledgesOnlyAfterHandlerReturnsSuccessfullyAndHonorsBatchLimit() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("k1", assignment("a"));
        queue.enqueue("k2", assignment("b"));
        queue.enqueue("k3", assignment("c"));
        AtomicInteger executions = new AtomicInteger();
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"),
                (claim, visibility) -> executions.incrementAndGet(), (task, failure) -> retry(failure),
                2, Duration.ofSeconds(30));

        TaskPollResult first = poller.pollOnce();
        assertEquals(2, first.claimed());
        assertEquals(2, first.completed());
        assertEquals(0, first.requeued());
        assertEquals(2, executions.get());
        assertEquals(1, poller.pollOnce().completed());
    }

    @Test
    void retryableFailureIsRedelivered() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("k1", assignment("a"));
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"),
                (claim, visibility) -> { throw new IllegalStateException("temporary"); },
                this::retry, 1, Duration.ofSeconds(30));

        TaskPollResult result = poller.pollOnce();
        assertEquals(1, result.requeued());
        assertEquals(0, result.deadLettered());
        assertEquals(1, queue.claim(WorkerId.of("retry-worker"), 1, Duration.ofSeconds(30)).size());
    }

    @Test
    void permanentFailureIsMovedToDeadLetterStore() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("k1", assignment("permanent"));
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"),
                (claim, visibility) -> { throw new IllegalArgumentException("invalid payload"); },
                (task, failure) -> TaskFailureDecision.deadLetter("PERMANENT", failure.getMessage()),
                1, Duration.ofSeconds(30));

        TaskPollResult result = poller.pollOnce();
        assertEquals(1, result.deadLettered());
        assertEquals(0, result.requeued());
        DeadLetterTask dead = queue.listDeadLetters(10).get(0);
        assertEquals("PERMANENT", dead.failureCategory());
        assertEquals("invalid payload", dead.failureMessage());
        assertTrue(queue.claim(WorkerId.of("another-worker"), 1, Duration.ofSeconds(30)).isEmpty());
    }

    @Test
    void retryableFailureMovesToDeadLetterAfterConfiguredRetryLimit() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("k1", assignment("exhausted"));
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"),
                (claim, visibility) -> { throw new IllegalStateException("transient"); },
                this::retry, 1, Duration.ofSeconds(30));

        assertEquals(1, poller.pollOnce().requeued());
        assertEquals(1, poller.pollOnce().requeued());
        TaskPollResult exhausted = poller.pollOnce();
        assertEquals(1, exhausted.deadLettered());
        DeadLetterTask dead = queue.listDeadLetters(1).get(0);
        assertEquals(3, dead.failureCount());
        assertEquals("TRANSIENT", dead.failureCategory());
    }

    @Test
    void staleClaimCannotRetryOrDeadLetterTask() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("k1", assignment("stale"));
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"),
                (claim, visibility) -> {
                    assertTrue(queue.negativeAcknowledge(claim.token(), Duration.ZERO));
                    throw new IllegalStateException("late failure");
                },
                (task, failure) -> TaskFailureDecision.deadLetter("PERMANENT", failure.getMessage()),
                1, Duration.ofSeconds(30));

        TaskPollResult result = poller.pollOnce();
        assertEquals(1, result.lostClaims());
        assertTrue(queue.listDeadLetters(10).isEmpty());
    }

    @Test
    void handlerCanExtendItsClaimVisibility() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("k1", assignment("a"));
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"),
                (claim, visibility) -> assertTrue(visibility.extend(Duration.ofSeconds(30))),
                this::retry, 1, Duration.ofSeconds(5));
        assertEquals(1, poller.pollOnce().completed());
    }

    @Test
    void validatesPollingConfiguration() {
        TaskQueue queue = new InMemoryTaskQueue();
        assertThrows(IllegalArgumentException.class, () -> new WorkerTaskPoller(queue,
                WorkerId.of("worker"), (claim, visibility) -> {}, this::retry,
                0, Duration.ofSeconds(1)));
        assertThrows(IllegalArgumentException.class, () -> new WorkerTaskPoller(queue,
                WorkerId.of("worker"), (claim, visibility) -> {}, this::retry,
                1, Duration.ZERO));
    }
}
