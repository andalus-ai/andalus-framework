package tech.kayys.andalus.tool.scheduling;

import tech.kayys.andalus.tool.ToolExecutionContext;
import tech.kayys.andalus.tool.ToolExecutionHandle;
import tech.kayys.andalus.tool.ToolExecutionPolicy;
import tech.kayys.andalus.tool.ToolExecutor;
import tech.kayys.andalus.tool.ToolInvocation;

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
