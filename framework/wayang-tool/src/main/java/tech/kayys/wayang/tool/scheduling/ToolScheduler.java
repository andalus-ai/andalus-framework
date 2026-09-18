package tech.kayys.wayang.tool.scheduling;

import tech.kayys.wayang.tool.ToolExecutionContext;
import tech.kayys.wayang.tool.ToolExecutionHandle;
import tech.kayys.wayang.tool.ToolExecutionPolicy;
import tech.kayys.wayang.tool.ToolExecutor;
import tech.kayys.wayang.tool.ToolInvocation;

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
