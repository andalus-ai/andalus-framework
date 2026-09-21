package tech.kayys.andalus.knowledge.exchange.framing;

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
 * Represents a knowledge evidence exchange frame header.
 *
 * <p>Its components capture `version`, `type`, `flags`, `sequence`, `payload length`.</p>
 *
 * @param version the version
 * @param type the type
 * @param flags the flags
 * @param sequence the sequence
 * @param payloadLength the payload length
 */



public record KnowledgeEvidenceExchangeFrameHeader(

        byte version,

        KnowledgeEvidenceExchangeFrameType type,

        int flags,

        long sequence,

        long payloadLength

) {
}
