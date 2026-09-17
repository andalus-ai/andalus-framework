package tech.kayys.wayang.execution.governance.approval;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory implementation of {@link ApprovalStore}.
 */
public final class InMemoryApprovalStore implements ApprovalStore {

    private final ConcurrentMap<String, ApprovalRequest> requests = new ConcurrentHashMap<>();

    @Override
    public void create(ApprovalRequest request) {
        if (requests.putIfAbsent(request.id(), request) != null) {
            throw new IllegalStateException("Approval already exists: " + request.id());
        }
    }

    @Override
    public Optional<ApprovalRequest> get(String approvalId) {
        if (approvalId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(requests.get(approvalId.trim()));
    }

    @Override
    public void update(ApprovalRequest request) {
        requests.compute(request.id(), (id, existing) -> {
            if (existing == null) {
                throw new IllegalStateException("Approval does not exist: " + id);
            }
            return request;
        });
    }

    @Override
    public void delete(String approvalId) {
        if (approvalId != null) {
            requests.remove(approvalId.trim());
        }
    }
}
