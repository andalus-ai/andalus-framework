package tech.kayys.andalus.knowledge.exchange.attestation;

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


import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge answer resolution consensus participant.
 *
 * <p>Its components capture `runtime id`, `tenant id`, `workspace id`, `project id`, `trusted`, and other values.</p>
 *
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param trusted the trusted
 * @param eligible the eligible
 * @param validFrom the valid from
 * @param validUntil the valid until
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionConsensusParticipant(

        String runtimeId,

        String tenantId,

        String workspaceId,

        String projectId,

        boolean trusted,

        boolean eligible,

        Instant validFrom,

        Instant validUntil,

        Map<String, String> metadata
) {

    public KnowledgeAnswerResolutionConsensusParticipant {

        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant time) {

        return trusted
                && eligible
                && (validFrom == null
                    || !time.isBefore(validFrom))
                && (validUntil == null
                    || time.isBefore(validUntil));
    }
}
