package tech.kayys.andalus.harness.fabric;

/** Signals that a task's execution fence or claim is no longer current. */
public final class StaleExecutionCompletionException extends Exception {
    public StaleExecutionCompletionException(String message) {
        super(message);
    }
}
