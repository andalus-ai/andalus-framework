package tech.kayys.andalus.execution.event;

/**
 * Durable, append-only ledger specifically for tool executions.
 */
public interface ToolExecutionLedger {

    /**
     * Records a tool execution.
     *
     * @param execution The execution record
     */
    void record(ToolExecution execution);

}
