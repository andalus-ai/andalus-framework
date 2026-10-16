package tech.kayys.andalus.memory.reactive;

import tech.kayys.andalus.memory.MemoryRecord;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

/**
 * A memory store that emits reactive events as memory changes.
 * Enables event-driven synchronization with conversation history,
 * cross-agent sharing, and durable consolidation.
 */
public interface ReactiveMemoryStore {

    /** Store a memory record and emit MemoryEvent.MemoryStored */
    CompletionStage<MemoryRecord> store(MemoryRecord record, String trigger);

    /** Recall relevant memory records for a query */
    CompletionStage<List<MemoryRecord>> recall(String agentId, String sessionId, String query, int limit);

    /** Observe a conversation turn — may trigger background consolidation */
    void observeTurn(String agentId, String sessionId, String turnId, String userInput, String agentOutput);

    /** Subscribe to memory events as a Flow publisher */
    Flow.Publisher<MemoryEvent> events();

    /** Register a synchronous listener */
    void addListener(MemoryEventListener listener);
    void removeListener(MemoryEventListener listener);
}
