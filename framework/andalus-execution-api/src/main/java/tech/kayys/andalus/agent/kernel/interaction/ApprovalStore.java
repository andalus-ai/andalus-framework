
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Optional;

public interface ApprovalStore {

    ApprovalRequest save(
            ApprovalRequest request
    );

    Optional<ApprovalRequest> find(
            String approvalId
    );

    ApprovalDecision decide(
            ApprovalDecision decision
    );

    Optional<ApprovalDecision> decision(
            String approvalId
    );
}
