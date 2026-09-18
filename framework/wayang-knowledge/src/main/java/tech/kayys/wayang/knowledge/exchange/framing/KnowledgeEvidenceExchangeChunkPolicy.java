package tech.kayys.wayang.knowledge.exchange.framing;

import tech.kayys.wayang.knowledge.*;
import tech.kayys.wayang.knowledge.seal.*;
import tech.kayys.wayang.knowledge.snapshot.*;
import tech.kayys.wayang.knowledge.snapshot.pack.*;
import tech.kayys.wayang.knowledge.snapshot.artifact.*;
import tech.kayys.wayang.knowledge.snapshot.merkle.*;
import tech.kayys.wayang.knowledge.exchange.*;
import tech.kayys.wayang.knowledge.exchange.auth.*;
import tech.kayys.wayang.knowledge.exchange.session.*;
import tech.kayys.wayang.knowledge.exchange.binding.*;
import tech.kayys.wayang.knowledge.exchange.envelope.*;
import tech.kayys.wayang.knowledge.exchange.trust.*;
import tech.kayys.wayang.knowledge.exchange.identity.*;
import tech.kayys.wayang.knowledge.exchange.capability.*;
import tech.kayys.wayang.knowledge.exchange.protocol.*;
import tech.kayys.wayang.knowledge.exchange.transport.*;
import tech.kayys.wayang.knowledge.exchange.framing.*;

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
