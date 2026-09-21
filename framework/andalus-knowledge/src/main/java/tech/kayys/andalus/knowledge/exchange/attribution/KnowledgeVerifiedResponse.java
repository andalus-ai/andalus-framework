package tech.kayys.andalus.knowledge.exchange.attribution;

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
 * Represents a knowledge verified response.
 *
 * <p>Its components capture `metadata`, `answer`, `status`, `disposition`, `confidence`, and other values.</p>
 *
 * @param metadata the metadata
 * @param answer the answer
 * @param status the status
 * @param disposition the disposition
 * @param confidence the confidence
 * @param claims the claims
 * @param evidence the evidence
 * @param blockedClaimIds the blocked claim ids
 * @param warnings the warnings
 * @param metadataMap the metadata map
 */


public record KnowledgeVerifiedResponse(

        KnowledgeVerifiedResponseMetadata metadata,

        String answer,

        KnowledgeVerifiedResponseStatus status,

        KnowledgeVerifiedResponseDisposition disposition,

        double confidence,

        List<KnowledgeVerifiedClaim> claims,

        List<KnowledgeVerifiedEvidenceReference> evidence,

        List<String> blockedClaimIds,

        List<String> warnings,

        Map<String, String> metadataMap
) {

    public KnowledgeVerifiedResponse {
        claims = claims == null
                ? List.of()
                : List.copyOf(claims);

        evidence = evidence == null
                ? List.of()
                : List.copyOf(evidence);

        blockedClaimIds = blockedClaimIds == null
                ? List.of()
                : List.copyOf(blockedClaimIds);

        warnings = warnings == null
                ? List.of()
                : List.copyOf(warnings);

        metadataMap = metadataMap == null
                ? Map.of()
                : Map.copyOf(metadataMap);
    }
}
