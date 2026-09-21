package tech.kayys.andalus.knowledge.exchange.framing;

import tech.kayys.andalus.knowledge.*;
import tech.kayys.andalus.knowledge.seal.*;
import tech.kayys.andalus.knowledge.snapshot.*;
import tech.kayys.andalus.knowledge.snapshot.pack.*;
import tech.kayys.andalus.knowledge.snapshot.artifact.*;
import tech.kayys.andalus.knowledge.snapshot.merkle.*;
import tech.kayys.andalus.knowledge.exchange.*;
import tech.kayys.andalus.knowledge.exchange.auth.*;
import tech.kayys.andalus.knowledge.exchange.session.*;
import tech.kayys.andalus.knowledge.exchange.binding.*;
import tech.kayys.andalus.knowledge.exchange.envelope.*;
import tech.kayys.andalus.knowledge.exchange.trust.*;
import tech.kayys.andalus.knowledge.exchange.identity.*;
import tech.kayys.andalus.knowledge.exchange.capability.*;
import tech.kayys.andalus.knowledge.exchange.protocol.*;
import tech.kayys.andalus.knowledge.exchange.transport.*;
import tech.kayys.andalus.knowledge.exchange.framing.*;

/**
 * Represents a knowledge evidence exchange chunk policy.
 *
 * <p>Its components capture `preferred chunk bytes`, `minimum chunk bytes`, `maximum chunk bytes`, `adaptive`.</p>
 *
 * @param preferredChunkBytes the preferred chunk bytes
 * @param minimumChunkBytes the minimum chunk bytes
 * @param maximumChunkBytes the maximum chunk bytes
 * @param adaptive the adaptive
 */



public record KnowledgeEvidenceExchangeChunkPolicy(

        int preferredChunkBytes,

        int minimumChunkBytes,

        int maximumChunkBytes,

        boolean adaptive

) {

    public static KnowledgeEvidenceExchangeChunkPolicy defaults() {

        return new KnowledgeEvidenceExchangeChunkPolicy(
                1024 * 1024,
                16 * 1024,
                8 * 1024 * 1024,
                true
        );
    }

    public int normalize(int requested) {

        if (requested <= 0) {
            return preferredChunkBytes;
        }

        return Math.max(
                minimumChunkBytes,
                Math.min(
                        requested,
                        maximumChunkBytes
                )
        );
    }
}
