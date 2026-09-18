package tech.kayys.wayang.harness.tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface ToolScheduler {

    ToolExecutionHandle submit(
            ToolInvocation invocation,
            ToolExecutionContext context,
            ToolExecutor executor,
            ToolExecutionPolicy policy
    );

    default ToolExecutionHandle submit(
            ToolInvocation invocation,
            ToolExecutionContext context,
            ToolExecutor executor
    ) {
        return submit(invocation, context, executor, ToolExecutionPolicy.defaults());
    }

    static ToolScheduler standard() {
        return new DefaultToolScheduler();
    }
}
