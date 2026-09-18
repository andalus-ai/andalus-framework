package tech.kayys.wayang.knowledge.exchange.sync;

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


import java.time.Instant;

/**
 * Represents a knowledge evidence artifact anti entropy round.
 *
 * <p>Its components capture `round id`, `local runtime id`, `remote runtime id`, `started at`, `completed at`, and other values.</p>
 *
 * @param roundId the round id
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param startedAt the started at
 * @param completedAt the completed at
 * @param state the state
 * @param repaired the repaired
 * @param failed the failed
 * @param localInventoryFingerprint the local inventory fingerprint
 * @param remoteInventoryFingerprint the remote inventory fingerprint
 */


public record KnowledgeEvidenceArtifactAntiEntropyRound(

        String roundId,

        String localRuntimeId,

        String remoteRuntimeId,

        Instant startedAt,

        Instant completedAt,

        KnowledgeEvidenceArtifactConsistencyState state,

        int repaired,

        int failed,

        String localInventoryFingerprint,

        String remoteInventoryFingerprint

) {}
