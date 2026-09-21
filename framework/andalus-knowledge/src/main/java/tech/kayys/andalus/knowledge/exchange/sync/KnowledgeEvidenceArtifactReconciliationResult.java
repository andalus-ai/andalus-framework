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
import java.util.Map;

/**
 * Represents a knowledge evidence artifact reconciliation result.
 *
 * <p>Its components capture `state`, `local runtime id`, `remote runtime id`, `missing locally`, `missing remotely`, and other values.</p>
 *
 * @param state the state
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param missingLocally the missing locally
 * @param missingRemotely the missing remotely
 * @param divergent the divergent
 * @param revoked the revoked
 * @param diagnostics the diagnostics
 */


public record KnowledgeEvidenceArtifactReconciliationResult(

        KnowledgeEvidenceArtifactConsistencyState state,

        String localRuntimeId,

        String remoteRuntimeId,

        List<String> missingLocally,

        List<String> missingRemotely,

        List<String> divergent,

        List<String> revoked,

        Map<String, String> diagnostics

) {

    public KnowledgeEvidenceArtifactReconciliationResult {

        missingLocally =
                missingLocally == null
                        ? List.of()
                        : List.copyOf(missingLocally);

        missingRemotely =
                missingRemotely == null
                        ? List.of()
                        : List.copyOf(missingRemotely);

        divergent =
                divergent == null
                        ? List.of()
                        : List.copyOf(divergent);

        revoked =
                revoked == null
                        ? List.of()
                        : List.copyOf(revoked);

        diagnostics =
                diagnostics == null
                        ? Map.of()
                        : Map.copyOf(diagnostics);
    }

    public boolean converged() {

        return state ==
                KnowledgeEvidenceArtifactConsistencyState.CONSISTENT;
    }
}
