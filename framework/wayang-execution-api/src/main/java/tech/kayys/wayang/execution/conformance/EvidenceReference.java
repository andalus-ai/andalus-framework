package tech.kayys.wayang.execution.conformance;

import java.util.Objects;

/**
 * Reference to verifiable evidence supporting a test finding or result.
 */
public record EvidenceReference(String evidenceId, String type, String location, String checksum) {

    public EvidenceReference {
        Objects.requireNonNull(evidenceId, "evidenceId cannot be null");
    }

    public static EvidenceReference of(String evidenceId, String type) {
        return new EvidenceReference(evidenceId, type, null, null);
    }

    public static EvidenceReference of(String evidenceId, String type, String location, String checksum) {
        return new EvidenceReference(evidenceId, type, location, checksum);
    }
}
