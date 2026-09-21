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


import java.util.List;

/**
 * Represents a knowledge evidence artifact synchronization plan.
 *
 * <p>Its components capture `pull`, `push`, `verify`, `revoked`.</p>
 *
 * @param pull the pull
 * @param push the push
 * @param verify the verify
 * @param revoked the revoked
 */


public record KnowledgeEvidenceArtifactSynchronizationPlan(

        List<String> pull,

        List<String> push,

        List<String> verify,

        List<String> revoked

) {

    public KnowledgeEvidenceArtifactSynchronizationPlan {

        pull = pull == null
                ? List.of()
                : List.copyOf(pull);

        push = push == null
                ? List.of()
                : List.copyOf(push);

        verify = verify == null
                ? List.of()
                : List.copyOf(verify);

        revoked = revoked == null
                ? List.of()
                : List.copyOf(revoked);
    }
}
