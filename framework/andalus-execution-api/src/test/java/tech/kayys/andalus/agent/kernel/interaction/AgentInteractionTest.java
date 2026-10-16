package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.kayys.andalus.agent.kernel.execution.AgentExecutionState;

import static org.junit.jupiter.api.Assertions.*;

class AgentInteractionTest {

    private AgentInteractionService service;

    @BeforeEach
    void setUp() {
        service = AgentInteractionService.inMemory();
    }

    @Test
    void approvalRoundTripPausesAndResumes() {
        InteractionContext ctx = service.context("run-1");
        ApprovalRequest req = ctx.requestApproval("need sign-off", "shell.exec", Map.of("cmd", "ls"));
        assertTrue(req.pending());
        assertEquals(AgentExecutionState.WAITING_FOR_APPROVAL, service.state("run-1"));

        ApprovalDecision decision = service.controller().grant(req.id(), "operator");
        assertEquals(ApprovalStatus.GRANTED, decision.status());
        assertEquals(AgentExecutionState.RUNNING, service.state("run-1"));
        assertEquals(4, service.history("run-1").size()); // paused + approval + granted + resumed
    }

    @Test
    void inputRoundTripPausesAndResumes() {
        InteractionContext ctx = service.context("run-2");
        InputRequest req = ctx.requestInput("Your name?", "text", Map.of(), true);
        assertEquals(AgentExecutionState.WAITING_FOR_INPUT, service.state("run-2"));

        InputResponse resp = service.controller().answer(req.id(), "Ada");
        assertEquals("Ada", resp.value());
        assertEquals(AgentExecutionState.RUNNING, service.state("run-2"));
        Optional<InputResponse> stored = service.inputResponse(req.id());
        assertTrue(stored.isPresent());
    }

    @Test
    void presentationIsPublished() {
        InteractionContext ctx = service.context("run-3");
        ctx.present(Presentation.markdown("p1", "# hello"));
        assertEquals(1, service.history("run-3").size());
        assertEquals(InteractionEventType.PRESENTATION, service.history("run-3").get(0).type());
    }
}
