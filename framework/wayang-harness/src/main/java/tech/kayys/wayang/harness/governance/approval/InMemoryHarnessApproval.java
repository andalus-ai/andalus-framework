package tech.kayys.wayang.harness.governance.approval;

import tech.kayys.wayang.harness.governance.action.HarnessAction;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory thread-safe implementation of {@link HarnessApproval}.
 */
public class InMemoryHarnessApproval implements HarnessApproval {

    private final Map<ApprovalId, ApprovalRequest> requests = new ConcurrentHashMap<>();
    private final Map<ApprovalId, ApprovalStatus> statuses = new ConcurrentHashMap<>();
    private final Map<ApprovalId, ApprovalGrant> grants = new ConcurrentHashMap<>();

    @Override
    public ApprovalRequest create(HarnessAction action, ApprovalContext context) {
        Objects.requireNonNull(action, "action");
        ApprovalId id = ApprovalId.generate();
        ApprovalRequest request = new ApprovalRequest(
                id,
                action,
                "Approval requested by: " + (context != null ? context.requestedBy() : "system"),
                context != null ? context.metadata() : Map.of(),
                Instant.now().plusSeconds(3600)
        );
        requests.put(id, request);
        statuses.put(id, ApprovalStatus.PENDING);
        return request;
    }

    @Override
    public ApprovalStatus status(ApprovalId id) {
        Objects.requireNonNull(id, "id");
        return statuses.getOrDefault(id, ApprovalStatus.EXPIRED);
    }

    @Override
    public void resolve(ApprovalId id, ApprovalResolution resolution) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(resolution, "resolution");

        ApprovalRequest req = requests.get(id);
        if (req == null) {
            throw new NoSuchElementException("Approval request not found: " + id.value());
        }

        if (resolution == ApprovalResolution.APPROVED) {
            statuses.put(id, ApprovalStatus.APPROVED);
            ApprovalGrant grant = new ApprovalGrant(id, req.action().id(), Instant.now(), req.expiresAt(), true);
            grants.put(id, grant);
        } else {
            statuses.put(id, ApprovalStatus.REJECTED);
            grants.remove(id);
        }
    }

    @Override
    public Optional<ApprovalGrant> grant(ApprovalId id) {
        Objects.requireNonNull(id, "id");
        ApprovalGrant grant = grants.get(id);
        if (grant != null && grant.isValid()) {
            return Optional.of(grant);
        }
        return Optional.empty();
    }
}
