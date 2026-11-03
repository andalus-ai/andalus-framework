package tech.kayys.andalus.harness.fabric;

/**
 * Cooperative variant of task work that can observe execution lease loss.
 * Implementations should check the context between units of work and register
 * callbacks for interruptible resources such as blocking I/O or subprocesses.
 */
@FunctionalInterface
public interface CooperativeFencedTaskExecutionWork {
    void execute(
            TaskClaim claim,
            ClaimVisibility visibility,
            tech.kayys.andalus.execution.mobility.FencingToken executionFence,
            ExecutionLeaseContext leaseContext) throws Exception;
}
