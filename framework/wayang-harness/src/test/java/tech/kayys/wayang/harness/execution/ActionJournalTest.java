package tech.kayys.wayang.harness.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.execution.action.*;
import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActionJournalTest {

    private InMemoryActionJournal journal;
    private ExecutionId executionId;

    @BeforeEach
    void setUp() {
        journal = new InMemoryActionJournal();
        executionId = ExecutionId.of("exec-303");
    }

    @Test
    void testActionLifecycleInJournal() {
        ActionId actId = ActionId.of("act-1");
        ActionRecord started = ActionRecord.started(actId, executionId, "git.commit", ActionExecutionMode.NON_REPLAYABLE);
        journal.started(started);

        Optional<ActionRecord> record = journal.find(actId);
        assertTrue(record.isPresent());
        assertEquals(ActionStatus.STARTED, record.get().status());

        // Complete action
        journal.completed(actId, "commit 4f9b8a");
        Optional<ActionRecord> completed = journal.find(actId);
        assertTrue(completed.isPresent());
        assertEquals(ActionStatus.COMPLETED, completed.get().status());
        assertEquals("commit 4f9b8a", completed.get().output().orElse(""));

        // History
        List<ActionRecord> history = journal.history(executionId);
        assertEquals(1, history.size());
        assertEquals(ActionStatus.COMPLETED, history.get(0).status());
    }

    @Test
    void testActionFailureInJournal() {
        ActionId actId = ActionId.of("act-fail");
        ActionRecord started = ActionRecord.started(actId, executionId, "http.request", ActionExecutionMode.AT_MOST_ONCE);
        journal.started(started);

        journal.failed(actId, "503 Service Unavailable");
        Optional<ActionRecord> failed = journal.find(actId);
        assertTrue(failed.isPresent());
        assertEquals(ActionStatus.FAILED, failed.get().status());
        assertEquals("503 Service Unavailable", failed.get().error().orElse(""));
    }
}
