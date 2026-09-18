package tech.kayys.wayang.harness.information.evidence;

import java.util.Objects;

/**
 * Verified atomic factual evidence piece that can be attached to context.
 */
public record Evidence(
        EvidenceId id,
        String text,
        EvidenceProvenance provenance
) {
    public Evidence {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(provenance, "provenance");
    }

    public static Evidence of(String text, String sourceUri) {
        return new Evidence(EvidenceId.generate(), text, EvidenceProvenance.direct(sourceUri));
    }
}
