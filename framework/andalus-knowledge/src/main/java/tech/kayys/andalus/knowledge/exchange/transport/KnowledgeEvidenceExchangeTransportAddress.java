package tech.kayys.andalus.knowledge.exchange.transport;

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
