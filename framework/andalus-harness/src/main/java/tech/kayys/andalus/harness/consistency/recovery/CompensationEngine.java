package tech.kayys.andalus.harness.consistency.recovery;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;

import java.util.List;
import java.util.Map;

/**
 * Orchestrates durable saga-style compensation for an execution.
 *
 * <p>Compensation is <em>not</em> rollback. It executes compensating operations for already-committed
 * effects in reverse successful order when the overall execution can no longer proceed normally (6.14).
 *
 * <p>The engine is responsible for:
 * <ul>
 *   <li>Recording compensation lifecycle facts in the execution journal ({@code COMPENSATION_STARTED}, etc.)</li>
 *   <li>Executing each compensatable step's {@link ExecutionStep#compensate} in reverse order</li>
 *   <li>Being idempotent: a crash during compensation allows the engine to resume from the last
 *       journaled compensation checkpoint on restart</li>
 * </ul>
 */
public interface CompensationEngine {

    /**
     * Runs compensation for an execution's successfully-completed steps in reverse order.
     *
     * @param executionId  identity of the execution being compensated
     * @param steps        ordered list of steps that completed successfully (will be compensated in reverse)
     * @param context      shared context map forwarded to each {@link ExecutionStep#compensate}
     * @return summary of the compensation run
     */
    CompensationSummary compensate(ExecutionId executionId, List<ExecutionStep> steps, Map<String, Object> context);
}
