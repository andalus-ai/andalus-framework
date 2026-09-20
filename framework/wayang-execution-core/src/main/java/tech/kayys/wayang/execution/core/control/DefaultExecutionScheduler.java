package tech.kayys.wayang.execution.core.control;

import tech.kayys.wayang.execution.control.ExecutionPriority;
import tech.kayys.wayang.execution.control.ExecutionScheduler;
import tech.kayys.wayang.execution.control.SchedulingDecision;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Standard implementation of {@link ExecutionScheduler} with priority-aware admission.
 */
public class DefaultExecutionScheduler implements ExecutionScheduler {

    private final String defaultRuntimeId;
    private final int maxConcurrentExecutions;
    private final AtomicInteger activeCount = new AtomicInteger(0);

    public DefaultExecutionScheduler(String defaultRuntimeId, int maxConcurrentExecutions) {
        this.defaultRuntimeId = Objects.requireNonNull(defaultRuntimeId, "defaultRuntimeId cannot be null");
        this.maxConcurrentExecutions = maxConcurrentExecutions > 0 ? maxConcurrentExecutions : 100;
    }

    public DefaultExecutionScheduler() {
        this("runtime-default", 100);
    }

    @Override
    public SchedulingDecision schedule(String executionId, ExecutionPriority priority) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        if (priority == null) {
            priority = ExecutionPriority.NORMAL;
        }

        // Critical priority bypasses queue capacity
        if (priority != ExecutionPriority.CRITICAL && activeCount.get() >= maxConcurrentExecutions) {
            return SchedulingDecision.rejected("Capacity exceeded: active executions at limit " + maxConcurrentExecutions);
        }

        activeCount.incrementAndGet();
        return SchedulingDecision.admitted(defaultRuntimeId, priority);
    }

    public void release() {
        activeCount.decrementAndGet();
    }
}
