package tech.kayys.wayang.harness.execution.action;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.util.List;
import java.util.Optional;

/**
 * Journaling contract recording each action's execution status, outputs, and errors for durability and recovery.
 */
public interface ActionJournal {

    void started(ActionRecord action);

    void completed(ActionId id, String output);

    void failed(ActionId id, String error);

    Optional<ActionRecord> find(ActionId id);

    List<ActionRecord> history(ExecutionId executionId);
}
