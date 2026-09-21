package tech.kayys.andalus.knowledge.exchange.trust;

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
import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for knowledge evidence exchange key trust registry operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeKeyTrustRegistry {

    void register(
            KnowledgeEvidenceExchangeTrustedKey key
    );

    Optional<KnowledgeEvidenceExchangeTrustedKey> find(
            String keyId,
            String keyVersion
    );

    Optional<KnowledgeEvidenceExchangeTrustedKey> resolve(
            String keyId,
            String keyVersion,
            Instant at
    );

    List<KnowledgeEvidenceExchangeTrustedKey> findByRuntime(
            String runtimeId
    );

    List<KnowledgeEvidenceExchangeTrustedKey> findByTenant(
            String tenantId
    );

    void revoke(
            String keyId,
            String keyVersion,
            String reason
    );

    boolean isTrusted(
            String keyId,
            String keyVersion,
            Instant at
    );
}
