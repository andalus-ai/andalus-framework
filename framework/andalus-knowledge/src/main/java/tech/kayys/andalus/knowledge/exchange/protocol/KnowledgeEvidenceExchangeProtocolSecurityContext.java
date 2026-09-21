package tech.kayys.andalus.knowledge.exchange.protocol;

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


import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence exchange protocol security context.
 *
 * <p>Its components capture `local runtime id`, `remote runtime id`, `tenant id`, `workspace id`, `project id`, and other values.</p>
 *
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param sessionId the session id
 * @param localKeyId the local key id
 * @param localKeyVersion the local key version
 * @param remoteKeyId the remote key id
 * @param remoteKeyVersion the remote key version
 * @param establishedAt the established at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeProtocolSecurityContext(

        String localRuntimeId,

        String remoteRuntimeId,

        String tenantId,

        String workspaceId,

        String projectId,

        String sessionId,

        String localKeyId,

        String localKeyVersion,

        String remoteKeyId,

        String remoteKeyVersion,

        Instant establishedAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeProtocolSecurityContext {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant at) {

        return expiresAt == null ||
                at.isBefore(expiresAt);
    }
}
