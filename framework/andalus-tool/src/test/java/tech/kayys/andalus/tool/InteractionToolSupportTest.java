package tech.kayys.andalus.tool;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.agent.kernel.interaction.AgentInteractionService;
import tech.kayys.andalus.agent.kernel.interaction.InteractionContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies tools can discover the interaction context without
 * depending on A2UI or the runtime bridge.
 */
class InteractionToolSupportTest {

    @Test
    void discoversInteractionFromContext() {
        InteractionContext ctx = AgentInteractionService.inMemory().context("run-1");
        ToolContext context = SimpleToolContext.of(Map.of(
                ToolContextKeys.INTERACTION, ctx,
                ToolContextKeys.RUN_ID, "run-1",
                ToolContextKeys.EXECUTION_ID, "exec-1"));

        assertEquals(Optional.of(ctx), InteractionToolSupport.interaction(context));
        assertEquals(Optional.of("run-1"), InteractionToolSupport.runId(context));
        assertEquals(Optional.of("exec-1"), InteractionToolSupport.executionId(context));
    }

    @Test
    void emptyWhenNoContext() {
        assertTrue(InteractionToolSupport.interaction(null).isEmpty());
        assertTrue(InteractionToolSupport.runId(SimpleToolContext.empty()).isEmpty());
    }
}
