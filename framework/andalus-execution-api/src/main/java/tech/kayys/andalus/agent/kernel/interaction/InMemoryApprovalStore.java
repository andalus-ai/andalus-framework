
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryApprovalStore
        implements ApprovalStore {

    private final Map<String, ApprovalRequest> requests =
            new ConcurrentHashMap<>();

    private final Map<String, ApprovalDecision> decisions =
            new ConcurrentHashMap<>();

    @Override
    public ApprovalRequest save(
            ApprovalRequest request
    ) {
        requests.put(
                request.id(),
                request
        );

        return request;
    }

    @Override
    public Optional<ApprovalRequest> find(
            String approvalId
    ) {
        return Optional.ofNullable(
                requests.get(approvalId)
        );
    }

    @Override
    public ApprovalDecision decide(
            ApprovalDecision decision
    ) {
        if (!requests.containsKey(decision.approvalId())) {
            throw new IllegalArgumentException(
                    "Unknown approval: "
                            + decision.approvalId()
            );
        }

        decisions.put(
                decision.approvalId(),
                decision
        );

        return decision;
    }

    @Override
    public Optional<ApprovalDecision> decision(
            String approvalId
    ) {
        return Optional.ofNullable(
                decisions.get(approvalId)
        );
    }
}
