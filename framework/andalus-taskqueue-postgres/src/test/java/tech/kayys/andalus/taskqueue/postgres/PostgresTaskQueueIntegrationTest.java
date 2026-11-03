package tech.kayys.andalus.taskqueue.postgres;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.postgresql.ds.PGSimpleDataSource;
import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.harness.fabric.*;
import tech.kayys.andalus.workflow.graph.NodeId;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostgresTaskQueueIntegrationTest {
    private DataSource configuredDataSource() {
        String url = System.getenv("ANDALUS_TEST_POSTGRES_URL");
        Assumptions.assumeTrue(url != null && !url.isBlank(),
                "Set ANDALUS_TEST_POSTGRES_URL to run PostgreSQL integration tests");
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setUrl(url);
        source.setUser(System.getenv("ANDALUS_TEST_POSTGRES_USER"));
        source.setPassword(System.getenv("ANDALUS_TEST_POSTGRES_PASSWORD"));
        return source;
    }

    @Test
    void claimGenerationFencesExpiredWorkerAndAcknowledgesCurrentOwner() throws Exception {
        DataSource source = configuredDataSource();
        PostgresTaskQueue queue = new PostgresTaskQueue(source);
        queue.ensureSchema();

        String suffix = UUID.randomUUID().toString();
        AssignmentId id = AssignmentId.of("pg-" + suffix);
        WorkerId assignedWorker = WorkerId.of("scheduler-" + suffix);
        ExecutionAssignment assignment = new DefaultExecutionAssignment(
                id, ExecutionId.of("exec-" + suffix), NodeId.of("node-" + suffix),
                AttemptId.initial(), assignedWorker, AssignmentFence.of(id, 1),
                AssignmentStatus.PENDING, AssignmentConstraints.defaults());

        try {
            queue.enqueue("idem-" + suffix, assignment);
            queue.enqueue("idem-" + suffix, assignment);

            WorkerId workerA = WorkerId.of("worker-a-" + suffix);
            TaskClaim first = queue.claim(workerA, 1, Duration.ofMinutes(1)).stream()
                    .filter(claim -> claim.assignment().id().equals(id)).findFirst().orElseThrow();

            try (Connection connection = source.getConnection();
                 PreparedStatement statement = connection.prepareStatement("""
                         UPDATE andalus_execution_task
                         SET claim_until = CURRENT_TIMESTAMP - INTERVAL '1 second'
                         WHERE assignment_id = ?
                         """)) {
                statement.setString(1, id.value());
                assertEquals(1, statement.executeUpdate());
            }

            WorkerId workerB = WorkerId.of("worker-b-" + suffix);
            TaskClaim second = queue.claim(workerB, 1, Duration.ofMinutes(1)).stream()
                    .filter(claim -> claim.assignment().id().equals(id)).findFirst().orElseThrow();

            assertEquals(first.token().generation() + 1, second.token().generation());
            assertFalse(queue.acknowledge(first.token()));
            assertTrue(queue.acknowledge(second.token()));
            assertTrue(queue.acknowledge(second.token()), "same exact claim token must be idempotent");
        } finally {
            try (Connection connection = source.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "DELETE FROM andalus_execution_task WHERE assignment_id = ?")) {
                statement.setString(1, id.value());
                statement.executeUpdate();
            }
        }
    }

    @Test
    void delayedNegativeAcknowledgementMakesTaskVisibleAgain() throws SQLException {
        DataSource source = configuredDataSource();
        PostgresTaskQueue queue = new PostgresTaskQueue(source);
        queue.ensureSchema();

        String suffix = UUID.randomUUID().toString();
        AssignmentId id = AssignmentId.of("pg-nack-" + suffix);
        ExecutionAssignment assignment = new DefaultExecutionAssignment(
                id, ExecutionId.of("exec-" + suffix), NodeId.of("node-" + suffix),
                AttemptId.initial(), WorkerId.of("scheduler-" + suffix),
                AssignmentFence.of(id, 1), AssignmentStatus.PENDING,
                AssignmentConstraints.defaults());
        try {
            queue.enqueue("idem-nack-" + suffix, assignment);
            TaskClaim first = queue.claim(WorkerId.of("worker-a-" + suffix), 1,
                    Duration.ofMinutes(1)).stream().filter(c -> c.assignment().id().equals(id))
                    .findFirst().orElseThrow();
            assertTrue(queue.negativeAcknowledge(first.token(), Duration.ZERO));
            TaskClaim second = queue.claim(WorkerId.of("worker-b-" + suffix), 1,
                    Duration.ofMinutes(1)).stream().filter(c -> c.assignment().id().equals(id))
                    .findFirst().orElseThrow();
            assertTrue(second.token().generation() > first.token().generation());
        } finally {
            try (Connection connection = source.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "DELETE FROM andalus_execution_task WHERE assignment_id = ?")) {
                statement.setString(1, id.value());
                statement.executeUpdate();
            }
        }
    }

    @Test
    void failureDispositionAtomicallyRetriesThenDeadLettersAfterRetryLimit() throws SQLException {
        DataSource source = configuredDataSource();
        PostgresTaskQueue queue = new PostgresTaskQueue(source);
        queue.ensureSchema();
        String suffix = UUID.randomUUID().toString();
        AssignmentId id = AssignmentId.of("pg-fail-" + suffix);
        ExecutionAssignment assignment = new DefaultExecutionAssignment(
                id, ExecutionId.of("exec-fail-" + suffix), NodeId.of("node-fail-" + suffix),
                AttemptId.initial(), WorkerId.of("scheduler-" + suffix), AssignmentFence.of(id, 1),
                AssignmentStatus.PENDING, AssignmentConstraints.defaults());
        try {
            queue.enqueue("idem-fail-" + suffix, assignment);
            WorkerId worker = WorkerId.of("worker-" + suffix);
            TaskClaim first = queue.claim(worker, 1, Duration.ofMinutes(1)).stream()
                    .filter(c -> c.assignment().id().equals(id)).findFirst().orElseThrow();
            assertEquals(TaskFailureResult.RETRY_SCHEDULED, queue.fail(first.token(),
                    TaskFailureDecision.retry(Duration.ZERO, "TRANSIENT", "first")));
            TaskClaim second = queue.claim(worker, 1, Duration.ofMinutes(1)).stream()
                    .filter(c -> c.assignment().id().equals(id)).findFirst().orElseThrow();
            assertEquals(TaskFailureResult.RETRY_SCHEDULED, queue.fail(second.token(),
                    TaskFailureDecision.retry(Duration.ZERO, "TRANSIENT", "second")));
            TaskClaim third = queue.claim(worker, 1, Duration.ofMinutes(1)).stream()
                    .filter(c -> c.assignment().id().equals(id)).findFirst().orElseThrow();
            assertEquals(TaskFailureResult.DEAD_LETTERED, queue.fail(third.token(),
                    TaskFailureDecision.retry(Duration.ZERO, "TRANSIENT", "exhausted")));
            DeadLetterTask dead = queue.listDeadLetters(100).stream()
                    .filter(task -> task.assignment().id().equals(id)).findFirst().orElseThrow();
            assertEquals(3, dead.failureCount());
            assertEquals("exhausted", dead.failureMessage());
            assertTrue(queue.claim(WorkerId.of("other-" + suffix), 1, Duration.ofMinutes(1)).stream()
                    .noneMatch(task -> task.assignment().id().equals(id)));
        } finally {
            try (Connection connection = source.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "DELETE FROM andalus_execution_task WHERE assignment_id = ?")) {
                statement.setString(1, id.value());
                statement.executeUpdate();
            }
        }
    }

    @Test
    void staleClaimCannotRecordFailureOrDeadLetter() throws SQLException {
        DataSource source = configuredDataSource();
        PostgresTaskQueue queue = new PostgresTaskQueue(source);
        queue.ensureSchema();
        String suffix = UUID.randomUUID().toString();
        AssignmentId id = AssignmentId.of("pg-stale-" + suffix);
        ExecutionAssignment assignment = new DefaultExecutionAssignment(
                id, ExecutionId.of("exec-stale-" + suffix), NodeId.of("node-stale-" + suffix),
                AttemptId.initial(), WorkerId.of("scheduler-" + suffix), AssignmentFence.of(id, 1),
                AssignmentStatus.PENDING, AssignmentConstraints.defaults());
        try {
            queue.enqueue("idem-stale-" + suffix, assignment);
            TaskClaim first = queue.claim(WorkerId.of("worker-a-" + suffix), 1,
                    Duration.ofMinutes(1)).stream().filter(c -> c.assignment().id().equals(id))
                    .findFirst().orElseThrow();
            assertTrue(queue.negativeAcknowledge(first.token(), Duration.ZERO));
            assertEquals(TaskFailureResult.LOST_CLAIM, queue.fail(first.token(),
                    TaskFailureDecision.deadLetter("PERMANENT", "late")));
            assertTrue(queue.listDeadLetters(100).stream()
                    .noneMatch(task -> task.assignment().id().equals(id)));
        } finally {
            try (Connection connection = source.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "DELETE FROM andalus_execution_task WHERE assignment_id = ?")) {
                statement.setString(1, id.value());
                statement.executeUpdate();
            }
        }
    }
}}
