package tech.kayys.andalus.telemetry;

import io.smallrye.mutiny.Uni;
import tech.kayys.andalus.telemetry.AuditPayload;

/**
 * Interface for storing audit events in a persistent provenance store (e.g.,
 * MongoDB, PostgreSQL).
 */
public interface ProvenanceStore {

    /**
     * Stores an audit payload.
     * 
     * @param payload the audit payload to store
     * @return a Uni representing the asynchronous completion
     */
    Uni<Void> store(AuditPayload payload);
}
