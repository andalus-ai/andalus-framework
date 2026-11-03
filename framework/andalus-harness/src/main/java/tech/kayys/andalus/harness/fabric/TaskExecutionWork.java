package tech.kayys.andalus.harness.fabric;

/** Performs the business/runtime work for one claimed assignment. */
@FunctionalInterface
public interface TaskExecutionWork {
    void execute(TaskClaim claim, ClaimVisibility visibility) throws Exception;
}
