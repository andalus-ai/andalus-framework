package tech.kayys.andalus.knowledge.exchange.federation;

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
import java.util.Map;

/**
 * Represents a knowledge evidence federation context.
 *
 * <p>Its components capture `query id`, `local runtime id`, `queried at`, `remote accessed`, `runtime count`, and other values.</p>
 *
 * @param queryId the query id
 * @param localRuntimeId the local runtime id
 * @param queriedAt the queried at
 * @param remoteAccessed the remote accessed
 * @param runtimeCount the runtime count
 * @param evidenceCount the evidence count
 * @param metadata the metadata
 */


public record KnowledgeEvidenceFederationContext(

        String queryId,

        String localRuntimeId,

        Instant queriedAt,

        boolean remoteAccessed,

        int runtimeCount,

        int evidenceCount,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceFederationContext {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
