package tech.kayys.andalus.agent.react;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.agent.AndalusAgentListener;
import tech.kayys.andalus.agent.PermissionDecision;
import tech.kayys.andalus.provider.Provider;
import tech.kayys.andalus.provider.StreamEvent;
import tech.kayys.andalus.provider.ToolSpec;
import tech.kayys.andalus.tool.CancellationToken;
import tech.kayys.andalus.tool.ToolResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReActAgentCancellationTest {

    @Test
    void cancelledBeforeSendDoesNotInvokeProvider() {
        CancellationToken cancellation = CancellationToken.create();
        cancellation.cancel();
        AtomicInteger providerCalls = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();

        Provider provider = provider((events) -> providerCalls.incrementAndGet());
        ReActAgent agent = new ReActAgent();
        agent.setProvider(provider);
        agent.setCancellationToken(cancellation);

        agent.send("hello", listener(new AtomicInteger(), errors, new AtomicInteger()));

        assertEquals(0, providerCalls.get());
        assertEquals(1, errors.get());
    }

    @Test
    void cancellationDuringStreamSuppressesLaterEventsAndCompletion() {
        CancellationToken cancellation = CancellationToken.create();
        AtomicInteger textEvents = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();
        AtomicInteger done = new AtomicInteger();

        Provider provider = provider(events -> {
            events.accept(new StreamEvent.TextDelta("before"));
            cancellation.cancel();
            events.accept(new StreamEvent.TextDelta("after"));
            events.accept(new StreamEvent.MessageStop("end_turn"));
        });

        ReActAgent agent = new ReActAgent();
        agent.setProvider(provider);
        agent.setCancellationToken(cancellation);

        agent.send("hello", listener(textEvents, errors, done));

        assertEquals(1, textEvents.get());
        assertEquals(1, errors.get());
        assertEquals(0, done.get());
    }

    private Provider provider(java.util.function.Consumer<java.util.function.Consumer<StreamEvent>> action) {
        return new Provider() {
            @Override
            public String id() {
                return "test";
            }

            @Override
            public void streamChat(List<tech.kayys.andalus.provider.ChatMessage> messages,
                                   String systemPrompt,
                                   List<ToolSpec> tools,
                                   double temperature,
                                   int maxTokens,
                                   java.util.function.Consumer<StreamEvent> onEvent) {
                action.accept(onEvent);
            }
        };
    }

    private AndalusAgentListener listener(AtomicInteger text, AtomicInteger errors, AtomicInteger done) {
        return new AndalusAgentListener() {
            @Override public void onTextDelta(String value) { text.incrementAndGet(); }
            @Override public void onToolCallStart(String id, String name) {}
            @Override public void onToolCallReady(String id, String name, Map<String, Object> input) {}
            @Override public void onToolPermissionNeeded(String id, String name, Map<String, Object> input,
                                                          java.util.function.Consumer<PermissionDecision> responder) {}
            @Override public void onToolResult(String id, String name, ToolResult result) {}
            @Override public void onUsage(int inputTokens, int outputTokens) {}
            @Override public void onDone(String stopReason) { done.incrementAndGet(); }
            @Override public void onError(String message) { errors.incrementAndGet(); }
        };
    }
}
