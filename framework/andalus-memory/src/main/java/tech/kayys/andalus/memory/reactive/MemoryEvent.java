package tech.kayys.andalus.memory.reactive;

import tech.kayys.andalus.memory.MemoryRecord;
import java.time.Instant;

/**
 * Sealed event type for reactive memory lifecycle.
 * Enables event-driven updates from conversation, tool calls, and agent turns.
 */
public sealed interface MemoryEvent
        permits MemoryEvent.MemoryStored,
                MemoryEvent.MemoryUpdated,
                MemoryEvent.MemoryExpired,
                MemoryEvent.MemoryEvicted,
                MemoryEvent.MemoryAccessed,
                MemoryEvent.ConversationTurnObserved {

    String agentId();
    String sessionId();
    Instant occurredAt();

    record MemoryStored(
            String agentId,
            String sessionId,
            MemoryRecord record,
            String trigger,
            Instant occurredAt
    ) implements MemoryEvent {}

    record MemoryUpdated(
            String agentId,
            String sessionId,
            MemoryRecord previous,
            MemoryRecord current,
            Instant occurredAt
    ) implements MemoryEvent {}

    record MemoryExpired(
            String agentId,
            String sessionId,
            String recordId,
            String recordKey,
            Instant occurredAt
    ) implements MemoryEvent {}

    record MemoryEvicted(
            String agentId,
            String sessionId,
            String recordId,
            String reason,
            Instant occurredAt
    ) implements MemoryEvent {}

    record MemoryAccessed(
            String agentId,
            String sessionId,
            String recordId,
            String query,
            Instant occurredAt
    ) implements MemoryEvent {}

    /** Emitted when a conversation turn ends — triggers memory consolidation */
    record ConversationTurnObserved(
            String agentId,
            String sessionId,
            String turnId,
            String userInput,
            String agentOutput,
            Instant occurredAt
    ) implements MemoryEvent {}
}
