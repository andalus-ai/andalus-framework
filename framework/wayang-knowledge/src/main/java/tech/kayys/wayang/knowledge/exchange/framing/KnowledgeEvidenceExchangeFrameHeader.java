package tech.kayys.wayang.knowledge.exchange.framing;

import tech.kayys.wayang.knowledge.*;
import tech.kayys.wayang.knowledge.seal.*;
import tech.kayys.wayang.knowledge.snapshot.*;
import tech.kayys.wayang.knowledge.snapshot.pack.*;
import tech.kayys.wayang.knowledge.snapshot.artifact.*;
import tech.kayys.wayang.knowledge.snapshot.merkle.*;
import tech.kayys.wayang.knowledge.exchange.*;
import tech.kayys.wayang.knowledge.exchange.auth.*;
import tech.kayys.wayang.knowledge.exchange.session.*;
import tech.kayys.wayang.knowledge.exchange.binding.*;
import tech.kayys.wayang.knowledge.exchange.envelope.*;
import tech.kayys.wayang.knowledge.exchange.trust.*;
import tech.kayys.wayang.knowledge.exchange.identity.*;
import tech.kayys.wayang.knowledge.exchange.capability.*;
import tech.kayys.wayang.knowledge.exchange.protocol.*;
import tech.kayys.wayang.knowledge.exchange.transport.*;
import tech.kayys.wayang.knowledge.exchange.framing.*;

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
