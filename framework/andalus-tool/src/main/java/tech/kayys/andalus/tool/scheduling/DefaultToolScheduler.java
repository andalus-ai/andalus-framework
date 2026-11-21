package tech.kayys.andalus.tool.scheduling;

import tech.kayys.andalus.tool.CancellationToken;
import tech.kayys.andalus.tool.DefaultToolExecutionHandle;
import tech.kayys.andalus.tool.ToolExecutionContext;
import tech.kayys.andalus.tool.ToolExecutionHandle;
import tech.kayys.andalus.tool.ToolExecutionPolicy;
import tech.kayys.andalus.tool.ToolExecutor;
import tech.kayys.andalus.tool.ToolInvocation;
import tech.kayys.andalus.tool.ToolResult;

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
        // Each scheduled invocation owns a cancellable child token. Reusing the
        // context token directly is unsafe: contexts may expose CancellationToken.none(),
        // whose cancel() is intentionally a no-op, and cancelling one tool must not
        // cancel sibling work sharing the parent context.
        CancellationToken cancellationToken = CancellationToken.create();
        CancellationToken parentCancellation = context != null ? context.cancellation() : null;
        CancellationToken.CancellationRegistration parentRegistration =
                parentCancellation != null
                        ? parentCancellation.onCancel(cancellationToken::cancel)
                        : () -> { };

        Duration timeout = policy != null && policy.timeout() != null ? policy.timeout() : Duration.ofSeconds(30);

        ScheduledFuture<?> timeoutTask = scheduler.schedule(() -> {
            if (!future.isDone()) {
                cancellationToken.cancel();
                future.complete(ToolResult.timeout(invocation.invocationIdentifier(), timeout));
            }
        }, timeout.toMillis(), TimeUnit.MILLISECONDS);

        future.whenComplete((result, error) -> {
            timeoutTask.cancel(false);
            parentRegistration.close();
        });

        executorService.submit(() -> {
            try {
                if (cancellationToken.isCancellationRequested()) {
                    future.complete(ToolResult.canceled(invocation.invocationIdentifier()));
                    return;
                }
                ToolResult result = executor.executeBlocking(invocation, context, cancellationToken);
                timeoutTask.cancel(true);
                future.complete(result);
            } catch (CancellationException ce) {
                timeoutTask.cancel(true);
                future.complete(ToolResult.canceled(invocation.invocationIdentifier()));
            } catch (Throwable t) {
                timeoutTask.cancel(true);
                future.complete(ToolResult.failure(invocation.invocationIdentifier(), t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName(), Duration.ZERO, "executor"));
            }
        });

        return new DefaultToolExecutionHandle(invocation.invocationIdentifier(), future, cancellationToken);
    }

    @Override
    public void close() {
        if (managesExecutor) {
            executorService.shutdown();
            scheduler.shutdown();
        }
    }
}
