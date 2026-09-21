package tech.kayys.andalus.knowledge.exchange.fusion;

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
 * Represents a knowledge evidence fusion result.
 *
 * <p>Its components capture `candidates`, `selected`, `relations`, `conflicts`, `coherent`, and other values.</p>
 *
 * @param candidates the candidates
 * @param selected the selected
 * @param relations the relations
 * @param conflicts the conflicts
 * @param coherent the coherent
 * @param ambiguous the ambiguous
 * @param diagnostics the diagnostics
 */


public record KnowledgeEvidenceFusionResult(

        List<KnowledgeEvidenceFusionCandidate> candidates,

        List<KnowledgeEvidenceFusionCandidate> selected,

        List<KnowledgeEvidenceFusionRelation> relations,

        List<KnowledgeEvidenceFusionConflict> conflicts,

        boolean coherent,

        boolean ambiguous,

        Map<String, String> diagnostics

) {

    public KnowledgeEvidenceFusionResult {

        candidates = candidates == null
                ? List.of()
                : List.copyOf(candidates);

        selected = selected == null
                ? List.of()
                : List.copyOf(selected);

        relations = relations == null
                ? List.of()
                : List.copyOf(relations);

        conflicts = conflicts == null
                ? List.of()
                : List.copyOf(conflicts);

        diagnostics = diagnostics == null
                ? Map.of()
                : Map.copyOf(diagnostics);
    }
}
