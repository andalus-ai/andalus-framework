package tech.kayys.andalus.execution.control;

/**
 * Monitors execution health across runtime, resources, and progress.
 */
public interface ExecutionHealthMonitor {

    HealthSnapshot inspect(String executionId);
}
