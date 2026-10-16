package tech.kayys.andalus.a2a.handoff;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;

class AgentHandoffTest {

    static HandoffContext ctx() {
        return HandoffContext.minimal("exec-1", "session-1", "original query");
    }

    @Test void handoffRecordGeneratesId() {
        var h = AgentHandoff.of("a1", "a2", ctx(), HandoffReason.SKILL_DELEGATION);
        assertNotNull(h.handoffId());
        assertEquals("a1", h.sourceAgentId());
        assertEquals("a2", h.targetAgentId());
        assertEquals(HandoffReason.SKILL_DELEGATION, h.reason());
    }

    @Test void handoffToSkillSetsSkillField() {
        var h = AgentHandoff.toSkill("src", "tgt", "translate", ctx());
        assertEquals("translate", h.targetSkill());
        assertEquals(HandoffReason.SKILL_DELEGATION, h.reason());
    }

    @Test void handoffContextWithLastOutput() {
        var c = ctx().withLastOutput("agent response");
        assertEquals("agent response", c.lastAgentOutput());
        assertEquals("exec-1", c.executionId());
    }

    @Test void allowAllPolicyAllows() throws Exception {
        var policy = AgentHandoffPolicy.allowAll();
        var h = AgentHandoff.of("a1", "a2", ctx(), HandoffReason.ESCALATION);
        var decision = policy.evaluate(h).toCompletableFuture().get();
        assertTrue(decision.allowed());
    }

    @Test void denyDecisionCarriesReason() {
        var d = HandoffDecision.deny("tenant mismatch");
        assertFalse(d.allowed());
        assertEquals("tenant mismatch", d.reason());
    }

    @Test void handoffServiceDeniedByPolicy() throws Exception {
        AgentHandoffPolicy denyAll = h -> CompletableFuture.completedFuture(HandoffDecision.deny("not allowed"));
        AgentHandoffRouter router = h -> { throw new AssertionError("should not route"); };
        var service = new AgentHandoffService(denyAll, router);

        var result = service.initiate(AgentHandoff.of("a1", "a2", ctx(), HandoffReason.OUT_OF_SCOPE))
                .toCompletableFuture().get();
        assertFalse(result.accepted());
        assertTrue(result.reason().contains("not allowed"));
    }

    @Test void handoffServiceRoutesWhenAllowed() throws Exception {
        AgentHandoffRouter router = h -> CompletableFuture.completedFuture(
                HandoffResult.accepted(h.handoffId(), h.targetAgentId(), "task-123"));
        var service = new AgentHandoffService(AgentHandoffPolicy.allowAll(), router);

        var result = service.initiate(AgentHandoff.of("a1", "a2", ctx(), HandoffReason.SKILL_DELEGATION))
                .toCompletableFuture().get();
        assertTrue(result.accepted());
        assertEquals("task-123", result.assignedTaskId());
    }

    @Test void memorySnapshotIsImmutable() {
        var items = List.of(new HandoffMemorySnapshot.HandoffMemoryItem("k", "v", "fact", 0.9, Map.of()));
        var snap = new HandoffMemorySnapshot("a1", "s1", items, null);
        assertThrows(UnsupportedOperationException.class, () -> snap.items().add(null));
    }
}
