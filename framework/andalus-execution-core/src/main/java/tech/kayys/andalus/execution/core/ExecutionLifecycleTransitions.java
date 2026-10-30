package tech.kayys.andalus.execution.core;

import tech.kayys.andalus.execution.ExecutionLifecycle;

/**
 * Defines the allowed transitions for the invocation-level execution lifecycle.
 *
 * <p>This state machine is intentionally separate from processing phases and from the
 * harness/runtime durable execution state models.
 */
final class ExecutionLifecycleTransitions {

    private ExecutionLifecycleTransitions() {
    }

    static boolean isAllowed(ExecutionLifecycle from, ExecutionLifecycle to) {
        if (from == to) {
            return true;
        }

        return switch (from) {
            case CREATED -> to == ExecutionLifecycle.AUTHORIZING
                    || to == ExecutionLifecycle.CANCELLED;
            case AUTHORIZING -> to == ExecutionLifecycle.AUTHORIZED
                    || to == ExecutionLifecycle.FAILED
                    || to == ExecutionLifecycle.CANCELLED;
            case AUTHORIZED -> to == ExecutionLifecycle.WAITING_OBLIGATION
                    || to == ExecutionLifecycle.RUNNING
                    || to == ExecutionLifecycle.FAILED
                    || to == ExecutionLifecycle.CANCELLED;
            case WAITING_OBLIGATION -> to == ExecutionLifecycle.RUNNING
                    || to == ExecutionLifecycle.FAILED
                    || to == ExecutionLifecycle.CANCELLED;
            case RUNNING -> to == ExecutionLifecycle.SUSPENDED
                    || to == ExecutionLifecycle.COMPLETED
                    || to == ExecutionLifecycle.FAILED
                    || to == ExecutionLifecycle.CANCELLED;
            case SUSPENDED -> to == ExecutionLifecycle.RESUMING
                    || to == ExecutionLifecycle.FAILED
                    || to == ExecutionLifecycle.CANCELLED;
            case RESUMING -> to == ExecutionLifecycle.RUNNING
                    || to == ExecutionLifecycle.FAILED
                    || to == ExecutionLifecycle.CANCELLED;
            case COMPLETED, FAILED, CANCELLED -> false;
        };
    }
}
