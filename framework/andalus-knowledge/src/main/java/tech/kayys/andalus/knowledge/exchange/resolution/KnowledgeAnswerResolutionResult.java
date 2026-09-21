package tech.kayys.andalus.knowledge.exchange.resolution;

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
 * Represents a knowledge answer resolution result.
 *
 * <p>Its components capture `resolution id`, `status`, `decision`, `preferred artifact id`, `accepted artifact ids`, and other values.</p>
 *
 * @param resolutionId the resolution id
 * @param status the status
 * @param decision the decision
 * @param preferredArtifactId the preferred artifact id
 * @param acceptedArtifactIds the accepted artifact ids
 * @param rejectedArtifactIds the rejected artifact ids
 * @param conflictingArtifactIds the conflicting artifact ids
 * @param confidence the confidence
 * @param candidates the candidates
 * @param relations the relations
 * @param diagnostics the diagnostics
 */


public record KnowledgeAnswerResolutionResult(

        String resolutionId,

        KnowledgeAnswerResolutionStatus status,

        KnowledgeAnswerResolutionDecision decision,

        String preferredArtifactId,

        List<String> acceptedArtifactIds,

        List<String> rejectedArtifactIds,

        List<String> conflictingArtifactIds,

        double confidence,

        List<KnowledgeAnswerResolutionCandidate> candidates,

        List<KnowledgeAnswerArtifactRelation> relations,

        Map<String, String> diagnostics
) {

    public KnowledgeAnswerResolutionResult {
        acceptedArtifactIds =
                acceptedArtifactIds == null
                        ? List.of()
                        : List.copyOf(acceptedArtifactIds);

        rejectedArtifactIds =
                rejectedArtifactIds == null
                        ? List.of()
                        : List.copyOf(rejectedArtifactIds);

        conflictingArtifactIds =
                conflictingArtifactIds == null
                        ? List.of()
                        : List.copyOf(conflictingArtifactIds);

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(candidates);

        relations =
                relations == null
                        ? List.of()
                        : List.copyOf(relations);

        diagnostics =
                diagnostics == null
                        ? Map.of()
                        : Map.copyOf(diagnostics);
    }
}
