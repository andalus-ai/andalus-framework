package tech.kayys.wayang.harness.governance.approval;

import tech.kayys.wayang.harness.governance.action.HarnessAction;

import java.util.Optional;

/**
 * Dedicated SPI managing human-in-the-loop or external authority approval requests and grants.
 */
public interface HarnessApproval {

    ApprovalRequest create(HarnessAction action, ApprovalContext context);

    ApprovalStatus status(ApprovalId id);

    void resolve(ApprovalId id, ApprovalResolution resolution);

    Optional<ApprovalGrant> grant(ApprovalId id);
}
