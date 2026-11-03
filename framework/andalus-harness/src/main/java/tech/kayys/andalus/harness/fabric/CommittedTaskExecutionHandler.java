package tech.kayys.andalus.harness.fabric;

import java.util.Objects;

/**
 * Runs task work and commits durable execution/attempt completion before returning
 * to the poller, which then acknowledges the queue claim.
 */
public final class CommittedTaskExecutionHandler implements TaskExecutionHandler {
    private final TaskExecutionWork work;
    private final ExecutionCompletionCommitter committer;

    public CommittedTaskExecutionHandler(TaskExecutionWork work,
                                         ExecutionCompletionCommitter committer) {
        this.work = Objects.requireNonNull(work, "work cannot be null");
        this.committer = Objects.requireNonNull(committer, "committer cannot be null");
    }

    @Override
    public void execute(TaskClaim claim, ClaimVisibility visibility) throws Exception {
        Objects.requireNonNull(claim, "claim cannot be null");
        Objects.requireNonNull(visibility, "visibility cannot be null");
        work.execute(claim, visibility);
        try {
            committer.commitCompleted(claim.assignment(), claim.token());
        } catch (StaleExecutionCompletionException stale) {
            throw stale;
        }
    }
}
