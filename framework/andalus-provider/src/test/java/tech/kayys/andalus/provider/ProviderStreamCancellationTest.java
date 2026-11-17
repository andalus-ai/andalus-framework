package tech.kayys.andalus.provider;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ProviderStreamCancellationTest {

    @Test
    void cancellationInterruptsLegacyBlockingProviderWorker() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch interrupted = new CountDownLatch(1);

        Provider provider = new Provider() {
            @Override
            public String id() {
                return "blocking-test";
            }

            @Override
            public void streamChat(List<ChatMessage> messages, String systemPrompt,
                                   List<ToolSpec> tools, double temperature, int maxTokens,
                                   java.util.function.Consumer<StreamEvent> onEvent)
                    throws IOException, InterruptedException {
                started.countDown();
                try {
                    new CountDownLatch(1).await();
                } catch (InterruptedException cancelled) {
                    interrupted.countDown();
                    throw cancelled;
                }
            }
        };

        ProviderStreamHandle handle = provider.streamChatCancellable(
                List.of(), "", List.of(), 0.0, 32, event -> { });

        assertTrue(started.await(2, TimeUnit.SECONDS));
        assertTrue(handle.cancel());
        assertTrue(interrupted.await(2, TimeUnit.SECONDS));
        assertTrue(handle.isCancelled());
        assertThrows(CancellationException.class,
                () -> handle.completion().toCompletableFuture().get(2, TimeUnit.SECONDS));
        assertFalse(handle.cancel(), "only the first cancellation request should be accepted");
    }

    @Test
    void callbacksAreSuppressedAfterCancellation() throws Exception {
        CountDownLatch firstEvent = new CountDownLatch(1);
        CountDownLatch interrupted = new CountDownLatch(1);
        AtomicInteger callbacks = new AtomicInteger();

        Provider provider = new Provider() {
            @Override
            public String id() {
                return "callback-test";
            }

            @Override
            public void streamChat(List<ChatMessage> messages, String systemPrompt,
                                   List<ToolSpec> tools, double temperature, int maxTokens,
                                   java.util.function.Consumer<StreamEvent> onEvent)
                    throws IOException, InterruptedException {
                onEvent.accept(new StreamEvent.TextDelta("before"));
                firstEvent.countDown();
                try {
                    new CountDownLatch(1).await();
                } catch (InterruptedException cancelled) {
                    interrupted.countDown();
                    onEvent.accept(new StreamEvent.TextDelta("after"));
                    throw cancelled;
                }
            }
        };

        ProviderStreamHandle handle = provider.streamChatCancellable(
                List.of(), "", List.of(), 0.0, 32, event -> callbacks.incrementAndGet());

        assertTrue(firstEvent.await(2, TimeUnit.SECONDS));
        assertEquals(1, callbacks.get());
        assertTrue(handle.cancel());
        assertTrue(interrupted.await(2, TimeUnit.SECONDS));
        assertThrows(CancellationException.class,
                () -> handle.completion().toCompletableFuture().get(2, TimeUnit.SECONDS));
        assertEquals(1, callbacks.get(), "no callbacks should be delivered after cancellation");
    }
}
