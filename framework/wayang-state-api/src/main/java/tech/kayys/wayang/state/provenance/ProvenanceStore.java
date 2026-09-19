package tech.kayys.wayang.state.provenance;

import java.util.List;

public interface ProvenanceStore {
    void append(ProvenanceRecord record);
    List<ProvenanceRecord> query(ProvenanceQuery query);
    List<ProvenanceRecord> findBySubject(ProvenanceSubject subject);
}
