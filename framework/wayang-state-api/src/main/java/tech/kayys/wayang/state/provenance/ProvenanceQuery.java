package tech.kayys.wayang.state.provenance;

import java.time.Instant;
import java.util.Optional;

public record ProvenanceQuery(
        Optional<ProvenanceSubject> subject,
        Optional<String> actorId,
        Optional<String> operationName,
        Optional<Instant> since,
        Optional<Instant> until,
        int limit
) {
    public static ProvenanceQuery forSubject(ProvenanceSubject subject) {
        return new ProvenanceQuery(Optional.of(subject), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 100);
    }

    public static ProvenanceQuery all() {
        return new ProvenanceQuery(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 100);
    }
}
