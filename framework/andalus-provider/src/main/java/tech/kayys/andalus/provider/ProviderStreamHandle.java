package tech.kayys.andalus.provider;

import java.util.concurrent.CompletionStage;

/**
 * Handle for a provider streaming operation.
 *
 * <p>{@link #cancel()} requests cancellation. The default Provider adapter
 * interrupts its worker thread; transport-native providers should override
 * Provider.streamChatCancellable and abort the underlying request. Completion
 * indicates that the provider operation has terminated, not merely that
 * cancellation was requested.</p>
 */
public interface ProviderStreamHandle {

    /**
     * Completes when the provider stream terminates, exceptionally on failure
     * or cancellation.
     */
    CompletionStage<Void> completion();

    /**
     * Requests cancellation. Returns true only for the first accepted request.
     */
    boolean cancel();

    /**
     * @return whether cancellation has been requested
     */
    boolean isCancelled();
}
