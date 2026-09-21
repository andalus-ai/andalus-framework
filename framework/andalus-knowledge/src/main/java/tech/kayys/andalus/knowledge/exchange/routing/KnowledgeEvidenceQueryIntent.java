package tech.kayys.andalus.knowledge.exchange.routing;

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
import tech.kayys.andalus.knowledge.exchange.transfer.*;
import tech.kayys.andalus.knowledge.exchange.replication.*;
import tech.kayys.andalus.knowledge.exchange.sync.*;
import tech.kayys.andalus.knowledge.exchange.federation.*;
import tech.kayys.andalus.knowledge.exchange.routing.*;
import tech.kayys.andalus.knowledge.exchange.fusion.*;


import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence query intent.
 *
 * <p>Its components capture `original query`, `concepts`, `entities`, `keywords`, `filters`, and other values.</p>
 *
 * @param originalQuery the original query
 * @param concepts the concepts
 * @param entities the entities
 * @param keywords the keywords
 * @param filters the filters
 * @param metadata the metadata
 */


public record KnowledgeEvidenceQueryIntent(

        String originalQuery,

        List<String> concepts,

        List<String> entities,

        List<String> keywords,

        List<String> filters,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceQueryIntent {

        concepts = concepts == null
                ? List.of()
                : List.copyOf(concepts);

        entities = entities == null
                ? List.of()
                : List.copyOf(entities);

        keywords = keywords == null
                ? List.of()
                : List.copyOf(keywords);

        filters = filters == null
                ? List.of()
                : List.copyOf(filters);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
