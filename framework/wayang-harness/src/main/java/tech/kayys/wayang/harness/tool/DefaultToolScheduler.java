package tech.kayys.wayang.harness.tool;

import java.time.Duration;
import java.util.concurrent.*;

public class DefaultToolScheduler implements ToolScheduler, AutoCloseable {

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;
    private final boolean managesExecutor;

    public DefaultToolScheduler(ExecutorService executorService, ScheduledExecutorService scheduler) {
        this.executorService = executorService != null ? executorService : Executors.newCachedThreadPool();
        this.scheduler = scheduler != null ? scheduler : Executors.newSingleThreadScheduledExecutor();
        this.managesExecutor = (executorService == null);
    }

    public DefaultToolScheduler() {
        this(null, null);
    }

    @Override
    public ToolExecutionHandle submit(
            ToolInvocation invocation,
            ToolExecutionContext context,
            ToolExecutor executor,
            ToolExecutionPolicy policy
    ) {
        CompletableFuture<ToolResult> future = new CompletableFuture<>();
        CancellationToken cancellationToken = context.cancellation() != null ? context.cancellation() : CancellationToken.create();

        Duration timeout = policy != null && policy.timeout() != null ? policy.timeout() : Duration.ofSeconds(30);

        ScheduledFuture<?> timeoutTask = scheduler.schedule(() -> {
            if (!future.isDone()) {
                cancellationToken.cancel();
                future.complete(ToolResult.timeout(invocation.id(), timeout));
            }
        }, timeout.toMillis(), TimeUnit.MILLISECONDS);

        executorService.submit(() -> {
            try {
                if (cancellationToken.isCancellationRequested()) {
                    future.complete(ToolResult.canceled(invocation.id()));
                    return;
                }
                ToolResult result = executor.execute(invocation, context);
                timeoutTask.cancel(true);
                future.complete(result);
            } catch (CancellationException ce) {
                timeoutTask.cancel(true);
                future.complete(ToolResult.canceled(invocation.id()));
            } catch (Throwable t) {
                timeoutTask.cancel(true);
                future.complete(ToolResult.failure(invocation.id(), t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName(), Duration.ZERO, "executor"));
            }
        });

        return new DefaultToolExecutionHandle(invocation.id(), future, cancellationToken);
    }

    @Override
    public void close() {
        if (managesExecutor) {
            executorService.shutdown();
            scheduler.shutdown();
        }
    }
}
