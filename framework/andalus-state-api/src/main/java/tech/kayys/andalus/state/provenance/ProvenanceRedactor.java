package tech.kayys.andalus.state.provenance;

public interface ProvenanceRedactor {
    ProvenanceRecord redact(ProvenanceRecord record);
}
