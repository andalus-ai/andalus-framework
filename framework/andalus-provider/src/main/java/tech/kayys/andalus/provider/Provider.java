package tech.kayys.andalus.provider;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import tech.kayys.andalus.resource.Modality;

/**
 * A pluggable AI backend. Implementations translate the normalized
 * {@link ChatMessage}/{@link ToolSpec} model into their own wire format,
 * stream the HTTP response, and emit normalized {@link StreamEvent}s.
 */
public interface Provider {

    String id();

    /**
     * Streams a chat completion. Blocks the calling thread until the
     * stream ends or an error occurs; callers typically invoke this on
     * a background thread and hop back to the UI thread inside onEvent.
     */
    void streamChat(
            List<ChatMessage> messages,
            String systemPrompt,
            List<ToolSpec> tools,
            double temperature,
            int maxTokens,
            Consumer<StreamEvent> onEvent
    ) throws IOException, InterruptedException;
    
    /**
     * Starts a cancellable stream without blocking the caller.
     *
     * <p>The compatibility implementation runs the legacy blocking
     * {@link #streamChat(List, String, List, double, int, Consumer)} method on a
     * virtual thread. Cancelling the returned handle interrupts that thread and
     * suppresses subsequent callbacks. Providers with transport-level cancellation
     * should override this method and abort their underlying HTTP/SSE request.</p>
     *
     * @return a handle for observing completion and requesting cancellation
     */
    default ProviderStreamHandle streamChatCancellable(
            List<ChatMessage> messages,
            String systemPrompt,
            List<ToolSpec> tools,
            double temperature,
            int maxTokens,
            Consumer<StreamEvent> onEvent) {
        AtomicBoolean cancelled = new AtomicBoolean(false);
        AtomicReference<Thread> worker = new AtomicReference<>();
        CompletableFuture<Void> completion = new CompletableFuture<>();

        Thread thread = Thread.ofVirtual().name("andalus-provider-" + id() + "-stream").unstarted(() -> {
            try {
                if (cancelled.get()) {
                    throw new CancellationException("Provider stream cancelled before start.");
                }
                streamChat(messages, systemPrompt, tools, temperature, maxTokens, event -> {
                    if (!cancelled.get()) {
                        onEvent.accept(event);
                    }
                });
                if (cancelled.get()) {
                    completion.completeExceptionally(new CancellationException("Provider stream cancelled."));
                } else {
                    completion.complete(null);
                }
            } catch (Throwable failure) {
                if (cancelled.get()) {
                    completion.completeExceptionally(new CancellationException("Provider stream cancelled."));
                } else {
                    completion.completeExceptionally(failure);
                }
                if (failure instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        worker.set(thread);
        thread.start();

        return new ProviderStreamHandle() {
            @Override
            public CompletionStage<Void> completion() {
                return completion;
            }

            @Override
            public boolean cancel() {
                if (!cancelled.compareAndSet(false, true)) {
                    return false;
                }
                Thread active = worker.get();
                if (active != null) {
                    active.interrupt();
                }
                return true;
            }

            @Override
            public boolean isCancelled() {
                return cancelled.get();
            }
        };
    }

    /**
     * @return the set of modalities this provider supports (defaults to TEXT only)
     */

    default Set<Modality> supportedModalities() {
        return Set.of(Modality.TEXT);
    }
}
