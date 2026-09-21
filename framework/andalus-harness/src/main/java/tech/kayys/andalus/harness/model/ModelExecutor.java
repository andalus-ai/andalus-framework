package tech.kayys.andalus.harness.model;

/**
 * Defines the contract for model executor operations in the Andalus framework.
 */


@FunctionalInterface
public interface ModelExecutor {

    ModelResult execute(
            ModelInvocation invocation,
            ModelExecutionContext context
    );
}
