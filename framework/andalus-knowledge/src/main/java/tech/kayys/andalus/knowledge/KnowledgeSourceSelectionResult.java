package tech.kayys.andalus.knowledge;
import java.util.List;

public record KnowledgeSourceSelectionResult(
        List<KnowledgeSourceSelection> selected,
        List<KnowledgeSourceSelection> excluded
) {
    public KnowledgeSourceSelectionResult {
        selected = selected == null ? List.of() : List.copyOf(selected);
        excluded = excluded == null ? List.of() : List.copyOf(excluded);
    }
    public static KnowledgeSourceSelectionResult empty() {
        return new KnowledgeSourceSelectionResult(List.of(), List.of());
    }
}
