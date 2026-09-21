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
 * Represents a knowledge evidence runtime knowledge descriptor.
 *
 * <p>Its components capture `runtime id`, `domains`, `knowledge types`, `tags`, `languages`, and other values.</p>
 *
 * @param runtimeId the runtime id
 * @param domains the domains
 * @param knowledgeTypes the knowledge types
 * @param tags the tags
 * @param languages the languages
 * @param estimatedCoverage the estimated coverage
 * @param metadata the metadata
 */


public record KnowledgeEvidenceRuntimeKnowledgeDescriptor(

        String runtimeId,

        List<String> domains,

        List<String> knowledgeTypes,

        List<String> tags,

        List<String> languages,

        double estimatedCoverage,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceRuntimeKnowledgeDescriptor {

        domains = domains == null
                ? List.of()
                : List.copyOf(domains);

        knowledgeTypes = knowledgeTypes == null
                ? List.of()
                : List.copyOf(knowledgeTypes);

        tags = tags == null
                ? List.of()
                : List.copyOf(tags);

        languages = languages == null
                ? List.of()
                : List.copyOf(languages);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
