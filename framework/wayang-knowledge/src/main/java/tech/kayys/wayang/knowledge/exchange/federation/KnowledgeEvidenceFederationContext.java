package tech.kayys.wayang.knowledge.exchange.federation;

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
