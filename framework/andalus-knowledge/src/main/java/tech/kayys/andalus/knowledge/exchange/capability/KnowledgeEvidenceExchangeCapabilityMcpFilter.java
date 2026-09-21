package tech.kayys.andalus.knowledge.exchange.capability;

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
import java.util.Objects;

/**
 * Provides knowledge evidence exchange capability mcp filter behavior for the Wayang framework.
 */


public final class KnowledgeEvidenceExchangeCapabilityMcpFilter {

    private final KnowledgeEvidenceExchangeNegotiatedSessionValidator
            validator;

    public KnowledgeEvidenceExchangeCapabilityMcpFilter(
            KnowledgeEvidenceExchangeNegotiatedSessionValidator validator
    ) {
        this.validator =
                Objects.requireNonNull(validator);
    }

    public boolean canExpose(
            KnowledgeEvidenceExchangeNegotiatedSession session,
            KnowledgeEvidenceExchangeCapabilityType capability,
            Instant at
    ) {

        return validator.supports(
                session,
                capability,
                at
        );
    }
}
