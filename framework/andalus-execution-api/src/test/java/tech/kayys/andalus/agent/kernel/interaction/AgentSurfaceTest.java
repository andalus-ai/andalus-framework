package tech.kayys.andalus.agent.kernel.interaction;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgentSurfaceTest {

    private AgentInteractionService service;

    @BeforeEach
    void setUp() {
        service = AgentInteractionService.inMemory();
    }

    @Test
    void surfaceRendersToBusAndUpdatesState() {
        InteractionSurface surface = service.surface("run-s1", "workbench");
        surface.update(Map.of("priority", "HIGH"));
        surface.present(Presentation.markdown("p1", "# hello"));

        assertEquals("HIGH", service.ui().state("run-s1", "workbench")
                .values().get("priority"));
        List<InteractionEvent> events = service.history("run-s1");
        assertTrue(events.stream().anyMatch(e ->
                e.type() == InteractionEventType.UI_STATE_CHANGED));
        assertTrue(events.stream().anyMatch(e ->
                e.type() == InteractionEventType.PRESENTATION));
    }

    @Test
    void bridgeProjectsPresentationAsA2ui() {
        java.util.List<PresentationEnvelope> seen = new java.util.ArrayList<>();
        A2uiPresentationBridge bridge = new A2uiPresentationBridge(seen::add);
        PresentationEnvelope env = new PresentationEnvelope(
                "e1", "run-b1",
                PresentationTarget.main("workbench"),
                Presentation.a2ui("p1", "# hi"),
                null, Map.of());
        InteractionEvent event = new InteractionEvent(
                0, "run-b1",
                InteractionEventType.PRESENTATION,
                java.time.Instant.now(),
                Map.of("envelope", env));
        bridge.accept(event);
        assertEquals(1, seen.size());
        assertTrue(seen.get(0).presentation().content().toString().contains("# hi"));
    }

    @Test
    void ledgerRoundTrip() {
        InteractionLedger ledger = new InMemoryInteractionLedger();
        InteractionRecord rec = ledger.append(new InteractionRecord(
                -1, "req-1", "run-l1",
                InteractionRequestType.APPROVAL,
                InteractionEventType.APPROVAL_REQUESTED,
                InteractionCorrelation.run("run-l1"),
                null, Map.of("k", "v")));
        assertEquals(0, rec.sequence());
        assertEquals(1, ledger.history("run-l1", -1).size());
    }
}
