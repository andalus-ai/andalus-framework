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
 * Represents a knowledge evidence exchange window update.
 *
 * <p>Its components capture `stream id`, `additional bytes`.</p>
 *
 * @param streamId the stream id
 * @param additionalBytes the additional bytes
 */



public record KnowledgeEvidenceExchangeWindowUpdate(

        String streamId,

        long additionalBytes

) {

    public KnowledgeEvidenceExchangeWindowUpdate {

        if (additionalBytes <= 0) {

            throw new IllegalArgumentException(
                    "additionalBytes must be positive"
            );
        }
    }
}
