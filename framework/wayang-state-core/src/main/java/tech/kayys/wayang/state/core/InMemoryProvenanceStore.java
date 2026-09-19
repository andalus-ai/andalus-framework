package tech.kayys.wayang.state.core;

import tech.kayys.wayang.state.provenance.ProvenanceQuery;
import tech.kayys.wayang.state.provenance.ProvenanceRecord;
import tech.kayys.wayang.state.provenance.ProvenanceStore;
import tech.kayys.wayang.state.provenance.ProvenanceSubject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class InMemoryProvenanceStore implements ProvenanceStore {

    private final List<ProvenanceRecord> records = new CopyOnWriteArrayList<>();

    @Override
    public void append(ProvenanceRecord record) {
        Objects.requireNonNull(record, "record cannot be null");
        records.add(record);
    }

    @Override
    public List<ProvenanceRecord> query(ProvenanceQuery query) {
        Stream<ProvenanceRecord> stream = records.stream();

        if (query.subject().isPresent()) {
            stream = stream.filter(r -> r.subject().equals(query.subject().get()));
        }
        if (query.actorId().isPresent()) {
            stream = stream.filter(r -> r.actor().id().equals(query.actorId().get()));
        }
        if (query.operationName().isPresent()) {
            stream = stream.filter(r -> r.operation().name().equals(query.operationName().get()));
        }
        if (query.since().isPresent()) {
            stream = stream.filter(r -> !r.timestamp().isBefore(query.since().get()));
        }
        if (query.until().isPresent()) {
            stream = stream.filter(r -> !r.timestamp().isAfter(query.until().get()));
        }

        return stream.limit(query.limit()).toList();
    }

    @Override
    public List<ProvenanceRecord> findBySubject(ProvenanceSubject subject) {
        return records.stream()
                .filter(r -> r.subject().equals(subject))
                .toList();
    }

    public List<ProvenanceRecord> all() {
        return List.copyOf(records);
    }
}
