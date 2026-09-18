package tech.kayys.wayang.harness.model;

@FunctionalInterface
public interface ModelExecutor {

    ModelResult execute(
            ModelInvocation invocation,
            ModelExecutionContext context
    );
}
