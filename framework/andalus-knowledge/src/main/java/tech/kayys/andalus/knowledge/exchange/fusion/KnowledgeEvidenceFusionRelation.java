package tech.kayys.andalus.knowledge.exchange.fusion;

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
import tech.kayys.andalus.knowledge.exchange.transfer.*;
import tech.kayys.andalus.knowledge.exchange.replication.*;
import tech.kayys.andalus.knowledge.exchange.sync.*;
import tech.kayys.andalus.knowledge.exchange.federation.*;
import tech.kayys.andalus.knowledge.exchange.routing.*;
import tech.kayys.andalus.knowledge.exchange.fusion.*;


import java.util.Map;

/**
 * Represents a knowledge evidence fusion relation.
 *
 * <p>Its components capture `source evidence id`, `target evidence id`, `type`, `confidence`, `reason`, and other values.</p>
 *
 * @param sourceEvidenceId the source evidence id
 * @param targetEvidenceId the target evidence id
 * @param type the type
 * @param confidence the confidence
 * @param reason the reason
 * @param metadata the metadata
 */


public record KnowledgeEvidenceFusionRelation(

        String sourceEvidenceId,

        String targetEvidenceId,

        KnowledgeEvidenceFusionRelationType type,

        double confidence,

        String reason,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceFusionRelation {

        if (sourceEvidenceId == null
                || targetEvidenceId == null) {

            throw new IllegalArgumentException(
                    "Evidence IDs are required"
            );
        }

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
