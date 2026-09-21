package tech.kayys.andalus.audit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.nio.file.Path;
import java.util.List;

import tech.kayys.andalus.core.Principal;
import tech.kayys.andalus.extension.Extension;

/**
 * Audit Provider - provides auditing.
 */
public interface AuditProvider extends Extension {
    
    /**
     * Record an audit event
     */
    void record(AuditEvent event) throws Exception;
    
    /**
     * Record with action and principal
     */
    default void record(String action, Principal principal, Object target) throws Exception {
        record(AuditEvent.builder()
            .action(action)
            .principal(principal)
            .targetType(target != null ? target.getClass().getSimpleName() : null)
            .targetId(target != null ? target.toString() : null)
            .build());
    }
    
    /**
     * Query audit events
     */
    default List<AuditEvent> query(AuditQuery query) throws Exception {
        return List.of();
    }
    
    /**
     * Export audit events
     */
    default void export(String format, AuditQuery query, Path path) throws Exception {
        // Optional
    }
}
