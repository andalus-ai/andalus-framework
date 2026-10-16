package tech.kayys.andalus.knowledge.pipeline;

import tech.kayys.andalus.knowledge.KnowledgeEvidence;
import tech.kayys.andalus.knowledge.RetrievalContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fuses and deduplicates multiple ranked candidate evidence lists (e.g. dense + sparse hybrid search).
 */
public interface EvidenceFusionStrategy {

    List<KnowledgeEvidence> fuse(
            List<List<KnowledgeEvidence>> candidateLists,
            RetrievalContext context
    );

    static EvidenceFusionStrategy deduplicating() {
        return (candidateLists, context) -> {
            if (candidateLists == null || candidateLists.isEmpty()) {
                return List.of();
            }
            List<KnowledgeEvidence> fused = new ArrayList<>();
            Set<String> seenItemIds = new HashSet<>();

            for (List<KnowledgeEvidence> list : candidateLists) {
                if (list == null) continue;
                for (KnowledgeEvidence ev : list) {
                    String id = ev.item() != null ? ev.item().id() : "";
                    if (id.isBlank() || seenItemIds.add(id)) {
                        fused.add(ev);
                    }
                }
            }
            return List.copyOf(fused);
        };
    }
}
