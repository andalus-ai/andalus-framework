package tech.kayys.andalus.agent.kernel.interaction;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import tech.kayys.andalus.agent.kernel.execution.AgentExecutionController;
import tech.kayys.andalus.agent.kernel.execution.AgentExecutionState;
import tech.kayys.andalus.agent.kernel.execution.AgentExecutionStateStore;
import tech.kayys.andalus.agent.kernel.execution.InMemoryAgentExecutionStateStore;

/**
 * Convenience facade wiring the Phase 4 interaction layer together.
 *
 * <p>Framework-level code stays dependency-free; runtimes expose this
 * as a CDI bean / service adapter (e.g. HITL bridge, REST resource).</p>
 */
public final class AgentInteractionService {

    private final InteractionBus bus;
    private final ApprovalStore approvals;
    private final InputStore inputs;
    private final AgentExecutionStateStore states;
    private final AgentExecutionController execution;
    private final InteractionController controller;
    private final InteractionContextFactory contexts;
    private final UiStateStore uiStates;
    private final UiStateController ui;
    private final PresentationRouter presentations;
    private final InteractionSurfaceFactory surfaces;
    private final InteractionLedger ledger;

    public AgentInteractionService(
            InteractionBus bus,
            ApprovalStore approvals,
            InputStore inputs,
            AgentExecutionStateStore states) {
        this(bus, approvals, inputs, states,
                new InMemoryUiStateStore(),
                new InMemoryInteractionLedger());
    }

    public AgentInteractionService(
            InteractionBus bus,
            ApprovalStore approvals,
            InputStore inputs,
            AgentExecutionStateStore states,
            UiStateStore uiStates,
            InteractionLedger ledger) {
        this.bus = bus;
        this.approvals = approvals;
        this.inputs = inputs;
        this.states = states;
        this.execution = new AgentExecutionController(states, bus);
        this.controller = new InteractionController(bus, approvals, inputs, execution);
        this.contexts = new InteractionContextFactory(controller);
        this.uiStates = uiStates;
        this.ui = new UiStateController(uiStates, bus);
        this.presentations = new InteractionPresentationRouter(bus);
        this.surfaces = new InteractionSurfaceFactory(presentations, ui);
        this.ledger = ledger;
    }

    public static AgentInteractionService inMemory() {
        return new AgentInteractionService(
                new InMemoryInteractionBus(),
                new InMemoryApprovalStore(),
                new InMemoryInputStore(),
                new InMemoryAgentExecutionStateStore());
    }

    public InteractionContext context(String runId) {
        execution.start(runId);
        return contexts.forRun(runId);
    }

    public InteractionBus bus() {
        return bus;
    }

    public ApprovalStore approvals() {
        return approvals;
    }

    public InputStore inputs() {
        return inputs;
    }

    public AgentExecutionStateStore states() {
        return states;
    }

    public AgentExecutionController execution() {
        return execution;
    }

    public InteractionController controller() {
        return controller;
    }

    public UiStateStore uiStates() {
        return uiStates;
    }

    public UiStateController ui() {
        return ui;
    }

    public PresentationRouter presentations() {
        return presentations;
    }

    public InteractionSurfaceFactory surfaces() {
        return surfaces;
    }

    public InteractionLedger ledger() {
        return ledger;
    }

    public InteractionSurface surface(String runId, String surfaceId) {
        execution.start(runId);
        return surfaces.open(runId, surfaceId);
    }

    public CorrelatedInteractionBus correlated(InteractionCorrelation correlation) {
        return new CorrelatedInteractionBus(ledger);
    }

    public CorrelatedInteractionBus correlatedBus() {
        return new CorrelatedInteractionBus(ledger);
    }

    public AgentExecutionState state(String runId) {
        return execution.state(runId);
    }

    public List<InteractionEvent> history(String runId) {
        return bus.history(runId, -1);
    }

    public Optional<ApprovalRequest> approval(String approvalId) {
        return approvals.find(approvalId);
    }

    public Optional<InputResponse> inputResponse(String requestId) {
        return inputs.response(requestId);
    }

    public InteractionEvent present(String runId, Presentation presentation) {
        return controller.present(runId, presentation);
    }

    public InteractionEvent presentText(String runId, String id, String text) {
        return present(runId, Presentation.text(id, text));
    }

    public InteractionEvent presentMarkdown(String runId, String id, String markdown) {
        return present(runId, Presentation.markdown(id, markdown));
    }

    public Map<String, Object> snapshot(String runId) {
        return Map.of(
                "runId", runId,
                "state", execution.state(runId).name(),
                "events", bus.history(runId, -1).size());
    }

    /** Factory for per-run {@link InteractionContext} handles. */
    public static final class InteractionContextFactory {
        private final InteractionController controller;

        public InteractionContextFactory(InteractionController controller) {
            this.controller = controller;
        }

        public InteractionContext forRun(String runId) {
            return new InteractionContext(runId, controller);
        }
    }
}
