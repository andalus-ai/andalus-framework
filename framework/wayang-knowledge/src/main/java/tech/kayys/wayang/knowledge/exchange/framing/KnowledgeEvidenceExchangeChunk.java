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


import java.util.Map;

/**
 * Represents a knowledge evidence exchange chunk.
 *
 * <p>Its components capture `artifact id`, `resource id`, `stream id`, `sequence`, `offset`, and other values.</p>
 *
 * @param artifactId the artifact id
 * @param resourceId the resource id
 * @param streamId the stream id
 * @param sequence the sequence
 * @param offset the offset
 * @param totalLength the total length
 * @param data the data
 * @param fingerprint the fingerprint
 * @param first the first
 * @param last the last
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeChunk(

        String artifactId,

        String resourceId,

        String streamId,

        long sequence,

        long offset,

        long totalLength,

        byte[] data,

        String fingerprint,

        boolean first,

        boolean last,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeChunk {

        data = data == null
                ? new byte[0]
                : data.clone();

        if (offset < 0) {
            throw new IllegalArgumentException(
                    "offset must be >= 0"
            );
        }

        if (totalLength < -1) {
            throw new IllegalArgumentException(
                    "totalLength must be >= -1"
            );
        }

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    @Override
    public byte[] data() {
        return data.clone();
    }
}
