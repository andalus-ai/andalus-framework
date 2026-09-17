package tech.kayys.wayang.execution.governance.approval;

import java.util.Optional;

/**
 * Storage interface for managing persistence of {@link ApprovalRequest} instances.
 */
public interface ApprovalStore {

    void create(ApprovalRequest request);

    Optional<ApprovalRequest> get(String approvalId);

    void update(ApprovalRequest request);

    void delete(String approvalId);
}
