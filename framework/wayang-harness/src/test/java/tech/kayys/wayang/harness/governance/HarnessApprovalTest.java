package tech.kayys.wayang.harness.governance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.governance.action.DefaultHarnessAction;
import tech.kayys.wayang.harness.governance.action.HarnessAction;
import tech.kayys.wayang.harness.governance.approval.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HarnessApprovalTest {

    private InMemoryHarnessApproval approval;

    @BeforeEach
    void setUp() {
        approval = new InMemoryHarnessApproval();
    }

    @Test
    void testCreateAndResolveApproval() {
        HarnessAction action = DefaultHarnessAction.of("deploy", "production.deploy");
        ApprovalRequest req = approval.create(action, ApprovalContext.of("deploy-agent"));

        assertNotNull(req);
        assertNotNull(req.id());
        assertEquals(ApprovalStatus.PENDING, approval.status(req.id()));
        assertTrue(approval.grant(req.id()).isEmpty());

        // Resolve positively
        approval.resolve(req.id(), ApprovalResolution.APPROVED);
        assertEquals(ApprovalStatus.APPROVED, approval.status(req.id()));

        Optional<ApprovalGrant> grant = approval.grant(req.id());
        assertTrue(grant.isPresent());
        assertTrue(grant.get().isValid());
        assertEquals(req.id(), grant.get().approvalId());
        assertEquals(action.id(), grant.get().actionId());
    }

    @Test
    void testRejectApproval() {
        HarnessAction action = DefaultHarnessAction.of("drop", "database.drop");
        ApprovalRequest req = approval.create(action, ApprovalContext.of("untrusted-agent"));

        approval.resolve(req.id(), ApprovalResolution.REJECTED);
        assertEquals(ApprovalStatus.REJECTED, approval.status(req.id()));
        assertTrue(approval.grant(req.id()).isEmpty());
    }
}
