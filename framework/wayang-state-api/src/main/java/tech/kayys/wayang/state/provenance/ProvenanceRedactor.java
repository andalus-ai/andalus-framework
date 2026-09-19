package tech.kayys.wayang.state.provenance;

public interface ProvenanceRedactor {
    ProvenanceRecord redact(ProvenanceRecord record);
}
