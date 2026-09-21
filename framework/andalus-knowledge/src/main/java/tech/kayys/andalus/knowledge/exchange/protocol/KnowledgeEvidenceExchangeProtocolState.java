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
 * Defines the knowledge evidence exchange protocol state values used by the Wayang framework.
 */



public enum KnowledgeEvidenceExchangeProtocolState {

    NEW,

    HELLO_SENT,

    HELLO_RECEIVED,

    IDENTITY_SENT,

    IDENTITY_RECEIVED,

    AUTHENTICATION_SENT,

    AUTHENTICATED,

    CAPABILITIES_SENT,

    CAPABILITIES_RECEIVED,

    NEGOTIATED,

    ESTABLISHED,

    EXCHANGING,

    CLOSE_SENT,

    CLOSED,

    FAILED
}
