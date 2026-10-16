package tech.kayys.andalus.knowledge.streaming;

import tech.kayys.andalus.knowledge.KnowledgeEvidence;
import tech.kayys.andalus.knowledge.KnowledgeSourceFailure;
import java.time.Instant;

/**
 * A sealed type for events emitted during streaming knowledge resolution.
 * Enables progressive rendering and early cancellation.
 */
public sealed interface KnowledgeStreamingEvent
        permits KnowledgeStreamingEvent.EvidenceArrived,
                KnowledgeStreamingEvent.SourceStarted,
                KnowledgeStreamingEvent.SourceCompleted,
                KnowledgeStreamingEvent.SourceFailed,
                KnowledgeStreamingEvent.ResolutionCompleted {

    Instant occurredAt();

    record EvidenceArrived(
            String sourceId,
            KnowledgeEvidence evidence,
            Instant occurredAt
    ) implements KnowledgeStreamingEvent {}

    record SourceStarted(
            String sourceId,
            Instant occurredAt
    ) implements KnowledgeStreamingEvent {}

    record SourceCompleted(
            String sourceId,
            int evidenceCount,
            long latencyMs,
            Instant occurredAt
    ) implements KnowledgeStreamingEvent {}

    record SourceFailed(
            KnowledgeSourceFailure failure,
            Instant occurredAt
    ) implements KnowledgeStreamingEvent {}

    record ResolutionCompleted(
            int totalEvidence,
            int selectedEvidence,
            long durationMs,
            boolean partial,
            Instant occurredAt
    ) implements KnowledgeStreamingEvent {}
}
