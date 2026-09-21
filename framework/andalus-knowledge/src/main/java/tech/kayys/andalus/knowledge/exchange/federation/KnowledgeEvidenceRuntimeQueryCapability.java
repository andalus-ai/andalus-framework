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


import java.util.Set;

/**
 * Represents a knowledge evidence runtime query capability.
 *
 * <p>Its components capture `runtime id`, `search`, `remote retrieval`, `governed retrieval`, `verification`, and other values.</p>
 *
 * @param runtimeId the runtime id
 * @param search the search
 * @param remoteRetrieval the remote retrieval
 * @param governedRetrieval the governed retrieval
 * @param verification the verification
 * @param partialVerification the partial verification
 * @param queryFormats the query formats
 */


public record KnowledgeEvidenceRuntimeQueryCapability(

        String runtimeId,

        boolean search,

        boolean remoteRetrieval,

        boolean governedRetrieval,

        boolean verification,

        boolean partialVerification,

        Set<String> queryFormats

) {

    public KnowledgeEvidenceRuntimeQueryCapability {

        queryFormats = queryFormats == null
                ? Set.of()
                : Set.copyOf(queryFormats);
    }
}
