package tech.kayys.andalus.knowledge.streaming;

import tech.kayys.andalus.knowledge.KnowledgeContext;
import tech.kayys.andalus.knowledge.KnowledgeEvidence;
import tech.kayys.andalus.knowledge.KnowledgeQuery;
import tech.kayys.andalus.knowledge.KnowledgeResolver;
import java.util.concurrent.Flow;

/**
 * Extension of KnowledgeResolver that supports streaming evidence delivery.
 * Backward-compatible: existing code using resolve() still works.
 * New consumers can subscribe to stream() for progressive evidence.
 */
public interface KnowledgeStreamingResolver extends KnowledgeResolver {

    /**
     * Stream evidence items progressively as sources return results.
     * Each emitted item is immediately available without waiting for all sources.
     */
    Flow.Publisher<KnowledgeEvidence> stream(KnowledgeQuery query, KnowledgeContext context);
}
