package tech.kayys.andalus.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.attempt.AttemptLineage;
import tech.kayys.andalus.execution.attempt.AttemptState;
import tech.kayys.andalus.execution.attempt.ExecutionAttempt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutionAttemptTest {

    @Test
    void testExecutionAttemptProgressionAndLineage() {
        String execId = "exec-42";
        ExecutionAttempt attempt1 = ExecutionAttempt.initial(execId, "worker-alpha");

        assertEquals(1, attempt1.number());
        assertEquals(AttemptState.INITIALIZING, attempt1.state());
        assertEquals("worker-alpha", attempt1.targetWorkerId());
        assertTrue(attempt1.finishedAt().isEmpty());

        ExecutionAttempt runningAttempt1 = attempt1.withState(AttemptState.RUNNING);
        assertEquals(AttemptState.RUNNING, runningAttempt1.state());

        ExecutionAttempt failedAttempt1 = runningAttempt1.withState(AttemptState.FAILED);
        assertEquals(AttemptState.FAILED, failedAttempt1.state());
        assertTrue(failedAttempt1.finishedAt().isPresent());

        // Physical attempt 2
        ExecutionAttempt attempt2 = failedAttempt1.next("worker-beta");
        assertEquals(2, attempt2.number());
        assertEquals(execId, attempt2.executionId());
        assertNotEquals(attempt1.attemptId(), attempt2.attemptId());
        assertEquals("worker-beta", attempt2.targetWorkerId());

        // Lineage
        AttemptLineage lineage = new AttemptLineage(
                execId,
                attempt2.attemptId(),
                Optional.of(attempt1.attemptId()),
                Optional.of("cp-checkpoint-01")
        );

        assertEquals(attempt2.attemptId(), lineage.attemptId());
        assertEquals(Optional.of(attempt1.attemptId()), lineage.parentAttemptId());
        assertEquals(Optional.of("cp-checkpoint-01"), lineage.restoredFromCheckpointId());
    }
}
