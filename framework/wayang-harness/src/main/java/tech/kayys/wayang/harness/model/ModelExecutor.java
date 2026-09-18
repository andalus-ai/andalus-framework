package tech.kayys.wayang.harness.model;

/**
 * Defines the contract for model executor operations in the Wayang framework.
 */


@FunctionalInterface
public interface ModelExecutor {

    ModelResult execute(
            ModelInvocation invocation,
            ModelExecutionContext context
    );
}
