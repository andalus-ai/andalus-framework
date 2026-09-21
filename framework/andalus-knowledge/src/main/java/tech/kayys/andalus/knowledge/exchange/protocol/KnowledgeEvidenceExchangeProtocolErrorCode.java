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

/**
 * Defines the knowledge evidence exchange protocol error code values used by the Wayang framework.
 */



public enum KnowledgeEvidenceExchangeProtocolErrorCode {

    INVALID_STATE,

    INVALID_MESSAGE,

    INVALID_PROTOCOL_VERSION,

    PROTOCOL_DOWNGRADE,

    UNKNOWN_RUNTIME,

    UNTRUSTED_RUNTIME,

    UNKNOWN_KEY,

    REVOKED_KEY,

    EXPIRED_KEY,

    INVALID_SIGNATURE,

    INVALID_SESSION,

    INVALID_NONCE,

    CAPABILITY_MISMATCH,

    REQUIRED_CAPABILITY_MISSING,

    NEGOTIATION_FAILED,

    SESSION_EXPIRED,

    SESSION_REVOKED,

    TENANT_MISMATCH,

    SCOPE_MISMATCH,

    REQUEST_REPLAYED,

    RESPONSE_MISMATCH,

    INTERNAL_ERROR
}
