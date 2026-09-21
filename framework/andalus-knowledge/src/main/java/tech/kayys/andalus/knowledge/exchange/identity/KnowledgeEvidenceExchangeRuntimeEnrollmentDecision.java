package tech.kayys.andalus.knowledge.exchange.identity;

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


import java.util.Map;

/**
 * Defines the contract for knowledge evidence exchange runtime enrollment decision operations in the Wayang framework.
 */


public sealed interface KnowledgeEvidenceExchangeRuntimeEnrollmentDecision
        permits
        KnowledgeEvidenceExchangeRuntimeEnrollmentDecision.Approved,
        KnowledgeEvidenceExchangeRuntimeEnrollmentDecision.Pending,
        KnowledgeEvidenceExchangeRuntimeEnrollmentDecision.Denied {

    record Approved(
            String policyId,
            Map<String, String> metadata
    ) implements KnowledgeEvidenceExchangeRuntimeEnrollmentDecision {

        public Approved {
            metadata = metadata == null
                    ? Map.of()
                    : Map.copyOf(metadata);
        }
    }

    record Pending(
            String policyId,
            String reason,
            Map<String, String> metadata
    ) implements KnowledgeEvidenceExchangeRuntimeEnrollmentDecision {

        public Pending {
            metadata = metadata == null
                    ? Map.of()
                    : Map.copyOf(metadata);
        }
    }

    record Denied(
            String policyId,
            String reason,
            Map<String, String> metadata
    ) implements KnowledgeEvidenceExchangeRuntimeEnrollmentDecision {

        public Denied {
            metadata = metadata == null
                    ? Map.of()
                    : Map.copyOf(metadata);
        }
    }
}
