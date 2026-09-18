package tech.kayys.wayang.knowledge.exchange.transport;

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
 * Represents a knowledge evidence exchange transport address.
 *
 * <p>Its components capture `type`, `scheme`, `host`, `port`, `path`, and other values.</p>
 *
 * @param type the type
 * @param scheme the scheme
 * @param host the host
 * @param port the port
 * @param path the path
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeTransportAddress(

        KnowledgeEvidenceExchangeTransportType type,

        String scheme,

        String host,

        Integer port,

        String path,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeTransportAddress {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public String endpoint() {

        if (port == null) {
            return scheme + "://" + host + pathOrEmpty();
        }

        return scheme
                + "://"
                + host
                + ":"
                + port
                + pathOrEmpty();
    }

    private String pathOrEmpty() {
        return path == null ? "" : path;
    }
}
