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


import java.util.Map;

/**
 * Represents a knowledge verified evidence reference.
 *
 * <p>Its components capture `evidence id`, `knowledge id`, `version id`, `artifact id`, `fragment id`, and other values.</p>
 *
 * @param evidenceId the evidence id
 * @param knowledgeId the knowledge id
 * @param versionId the version id
 * @param artifactId the artifact id
 * @param fragmentId the fragment id
 * @param provenanceId the provenance id
 * @param relevance the relevance
 * @param authority the authority
 * @param trust the trust
 * @param verified the verified
 * @param metadata the metadata
 */


public record KnowledgeVerifiedEvidenceReference(

        String evidenceId,

        String knowledgeId,

        String versionId,

        String artifactId,

        String fragmentId,

        String provenanceId,

        double relevance,

        double authority,

        double trust,

        boolean verified,

        Map<String, String> metadata
) {

    public KnowledgeVerifiedEvidenceReference {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
