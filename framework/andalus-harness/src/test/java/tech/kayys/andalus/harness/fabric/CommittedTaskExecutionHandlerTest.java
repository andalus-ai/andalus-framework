package tech.kayys.andalus.harness.fabric;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.workflow.graph.NodeId;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommittedTaskExecutionHandlerTest {
    private DefaultExecutionAssignment assignment(String id) {
        AssignmentId assignmentId = AssignmentId.of(id);
        return new DefaultExecutionAssignment(assignmentId, ExecutionId.of("exec-" + id),
                NodeId.of("node-" + id), AttemptId.initial(), WorkerId.of("scheduler"),
                AssignmentFence.of(assignmentId, 7), AssignmentStatus.PENDING,
                AssignmentConstraints.defaults());
    }

    @Test
    void commitsExecutionBeforePollerAcknowledgesQueueClaim() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("key", assignment("commit-order"));
        List<String> events = new ArrayList<>();
        TaskExecutionHandler handler = new CommittedTaskExecutionHandler(
                (claim, visibility) -> events.add("work"),
                (assignment, token) -> {
                    assertEquals(7, assignment.fence().generation());
                    assertEquals(assignment.id(), token.assignmentId());
                    events.add("durable-commit");
                });
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"), handler,
                (task, failure) -> TaskFailureDecision.retry(Duration.ZERO, "TRANSIENT", failure.getMessage()),
                1, Duration.ofSeconds(30));

        TaskPollResult result = poller.pollOnce();

        assertEquals(List.of("work", "durable-commit"), events);
        assertEquals(1, result.completed());
        assertTrue(queue.claim(WorkerId.of("other"), 1, Duration.ofSeconds(30)).isEmpty());
    }

    @Test
    void commitFailurePreventsAcknowledgementAndAllowsRetry() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("key", assignment("commit-failure"));
        TaskExecutionHandler handler = new CommittedTaskExecutionHandler(
                (claim, visibility) -> {},
                (assignment, token) -> { throw new IllegalStateException("state store unavailable"); });
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"), handler,
                (task, failure) -> TaskFailureDecision.retry(Duration.ZERO, "TRANSIENT", failure.getMessage()),
                1, Duration.ofSeconds(30));

        TaskPollResult result = poller.pollOnce();

        assertEquals(1, result.requeued());
        assertEquals(0, result.completed());
        assertEquals(1, queue.claim(WorkerId.of("retry-worker"), 1, Duration.ofSeconds(30)).size());
    }

    @Test
    void staleExecutionFenceIsNotReportedAsSuccessfulCompletion() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("key", assignment("stale-fence"));
        TaskExecutionHandler handler = new CommittedTaskExecutionHandler(
                (claim, visibility) -> {},
                (assignment, token) -> { throw new StaleExecutionCompletionException("assignment fence superseded"); });
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"), handler,
                (task, failure) -> TaskFailureDecision.deadLetter("STALE_EXECUTION_FENCE", failure.getMessage()),
                1, Duration.ofSeconds(30));

        TaskPollResult result = poller.pollOnce();

        assertEquals(1, result.deadLettered());
        assertEquals(0, result.completed());
        assertEquals("STALE_EXECUTION_FENCE", queue.listDeadLetters(1).get(0).failureCategory());
    }

    @Test
    void workFailureNeverInvokesCompletionCommitter() {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("key", assignment("work-failure"));
        List<String> events = new ArrayList<>();
        TaskExecutionHandler handler = new CommittedTaskExecutionHandler(
                (claim, visibility) -> { throw new IllegalStateException("tool failed"); },
                (assignment, token) -> events.add("commit"));
        WorkerTaskPoller poller = new WorkerTaskPoller(queue, WorkerId.of("worker"), handler,
                (task, failure) -> TaskFailureDecision.retry(Duration.ZERO, "TRANSIENT", failure.getMessage()),
                1, Duration.ofSeconds(30));

        TaskPollResult result = poller.pollOnce();

        assertTrue(events.isEmpty());
        assertEquals(1, result.requeued());
    }
}
