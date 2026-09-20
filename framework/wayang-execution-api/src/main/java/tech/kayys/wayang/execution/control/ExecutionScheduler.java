package tech.kayys.wayang.execution.control;

/**
 * Admits, prioritizes, and schedules executions across available runtimes.
 */
public interface ExecutionScheduler {

    SchedulingDecision schedule(String executionId, ExecutionPriority priority);
}
