package tech.kayys.wayang.execution.sandbox;

/**
 * Interceptor hook invoked during sandbox state transitions.
 */
public interface SandboxLifecycleHook {

    default void beforeStart(ExecutionSandbox sandbox) {}

    default void afterStart(ExecutionSandbox sandbox) {}

    default void beforeDestroy(ExecutionSandbox sandbox) {}

    default void afterDestroy(SandboxId id) {}

    default void onViolation(ExecutionSandbox sandbox, String violationReason) {}
}
