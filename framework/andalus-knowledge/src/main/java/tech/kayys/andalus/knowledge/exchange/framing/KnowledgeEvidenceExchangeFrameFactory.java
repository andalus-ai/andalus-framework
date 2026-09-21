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


import java.util.EnumSet;
import java.util.Map;

/**
 * Provides knowledge evidence exchange frame factory behavior for the Wayang framework.
 */


public final class KnowledgeEvidenceExchangeFrameFactory {

    private final KnowledgeEvidenceExchangePayloadFingerprinter
            fingerprinter;

    public KnowledgeEvidenceExchangeFrameFactory(
            KnowledgeEvidenceExchangePayloadFingerprinter
                    fingerprinter
    ) {
        this.fingerprinter = fingerprinter;
    }

    public KnowledgeEvidenceExchangeFrame create(
            KnowledgeEvidenceExchangeFrameType type,
            String sessionId,
            String streamId,
            String requestId,
            long sequence,
            byte[] payload,
            boolean first,
            boolean last,
            Map<String, String> metadata
    ) {

        var flags =
                EnumSet.noneOf(
                        KnowledgeEvidenceExchangeFrameFlags.class
                );

        if (first) {
            flags.add(
                    KnowledgeEvidenceExchangeFrameFlags.FIRST
            );
        }

        if (last) {

            flags.add(
                    KnowledgeEvidenceExchangeFrameFlags.LAST
            );

            flags.add(
                    KnowledgeEvidenceExchangeFrameFlags.FIN
            );
        }

        flags.add(
                KnowledgeEvidenceExchangeFrameFlags.CHECKSUMMED
        );

        return new KnowledgeEvidenceExchangeFrame(
                (byte) 1,
                type,
                flags,
                sessionId,
                streamId,
                requestId,
                sequence,
                payload.length,
                payload,
                fingerprinter.fingerprint(payload),
                metadata
        );
    }
}
