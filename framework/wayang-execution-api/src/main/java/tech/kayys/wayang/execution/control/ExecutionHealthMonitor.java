package tech.kayys.wayang.execution.control;

/**
 * Monitors execution health across runtime, resources, and progress.
 */
public interface ExecutionHealthMonitor {

    HealthSnapshot inspect(String executionId);
}
