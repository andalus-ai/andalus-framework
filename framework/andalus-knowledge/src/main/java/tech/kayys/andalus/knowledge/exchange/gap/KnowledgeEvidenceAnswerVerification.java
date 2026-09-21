package tech.kayys.andalus.knowledge.exchange.gap;

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
import tech.kayys.andalus.knowledge.exchange.coverage.*;
import tech.kayys.andalus.knowledge.exchange.gap.*;
import tech.kayys.andalus.knowledge.exchange.attribution.*;
import tech.kayys.andalus.knowledge.exchange.contradiction.*;
import tech.kayys.andalus.knowledge.exchange.factuality.*;
import tech.kayys.andalus.knowledge.exchange.uncertainty.*;
import tech.kayys.andalus.knowledge.exchange.compact.*;
import tech.kayys.andalus.knowledge.exchange.resolution.*;
import tech.kayys.andalus.knowledge.exchange.quorum.*;
import tech.kayys.andalus.knowledge.exchange.selection.*;
import tech.kayys.andalus.knowledge.exchange.coordination.*;
import tech.kayys.andalus.knowledge.exchange.attestation.*;
import tech.kayys.andalus.knowledge.exchange.proof.*;
import tech.kayys.andalus.knowledge.exchange.validity.*;
import tech.kayys.andalus.knowledge.exchange.lease.*;
import tech.kayys.andalus.knowledge.exchange.recovery.*;


import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence answer verification.
 *
 * <p>Its components capture `verification id`, `claim graph`, `claims`, `overall status`, `confidence`, and other values.</p>
 *
 * @param verificationId the verification id
 * @param claimGraph the claim graph
 * @param claims the claims
 * @param overallStatus the overall status
 * @param confidence the confidence
 * @param releasable the releasable
 * @param blockedClaimIds the blocked claim ids
 * @param diagnostics the diagnostics
 */


public record KnowledgeEvidenceAnswerVerification(

        String verificationId,

        KnowledgeEvidenceClaimGraph claimGraph,

        List<KnowledgeEvidenceClaimVerification> claims,

        KnowledgeEvidenceClaimStatus overallStatus,

        double confidence,

        boolean releasable,

        List<String> blockedClaimIds,

        Map<String, String> diagnostics
) {

    public KnowledgeEvidenceAnswerVerification {
        claims = claims == null
                ? List.of()
                : List.copyOf(claims);

        blockedClaimIds = blockedClaimIds == null
                ? List.of()
                : List.copyOf(blockedClaimIds);

        diagnostics = diagnostics == null
                ? Map.of()
                : Map.copyOf(diagnostics);
    }
}
