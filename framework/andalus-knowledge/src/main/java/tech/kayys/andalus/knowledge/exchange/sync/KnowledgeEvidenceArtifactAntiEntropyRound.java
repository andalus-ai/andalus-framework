package tech.kayys.andalus.knowledge.exchange.sync;

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
