package tech.kayys.wayang.knowledge.exchange.fusion;

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
import tech.kayys.wayang.knowledge.exchange.transfer.*;
import tech.kayys.wayang.knowledge.exchange.replication.*;
import tech.kayys.wayang.knowledge.exchange.sync.*;
import tech.kayys.wayang.knowledge.exchange.federation.*;
import tech.kayys.wayang.knowledge.exchange.routing.*;
import tech.kayys.wayang.knowledge.exchange.fusion.*;


import java.util.Map;

/**
 * Represents a knowledge evidence fusion conflict.
 *
 * <p>Its components capture `conflict id`, `left evidence id`, `right evidence id`, `type`, `confidence`, and other values.</p>
 *
 * @param conflictId the conflict id
 * @param leftEvidenceId the left evidence id
 * @param rightEvidenceId the right evidence id
 * @param type the type
 * @param confidence the confidence
 * @param reason the reason
 * @param metadata the metadata
 */


public record KnowledgeEvidenceFusionConflict(

        String conflictId,

        String leftEvidenceId,

        String rightEvidenceId,

        KnowledgeEvidenceConflictType type,

        double confidence,

        String reason,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceFusionConflict {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
