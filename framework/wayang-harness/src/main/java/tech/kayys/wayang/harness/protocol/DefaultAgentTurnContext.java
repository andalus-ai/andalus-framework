package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.context.ContextSnapshot;
import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.util.Objects;

/**
 * Represents a default agent turn context.
 *
 * <p>Its components capture `execution id`, `identity`, `context`, `capabilities`, `budget`, and other values.</p>
 *
 * @param executionId the execution id
 * @param identity the identity
 * @param context the context
 * @param capabilities the capabilities
 * @param budget the budget
 * @param tools the tools
 * @param models the models
 */


public record DefaultAgentTurnContext(
        ExecutionId executionId,
        HarnessIdentity identity,
        ContextSnapshot context,
        CapabilityView capabilities,
        BudgetView budget,
        ToolCatalogView tools,
        ModelCatalogView models
) implements AgentTurnContext {

    public DefaultAgentTurnContext {
        if (executionId == null) executionId = ExecutionId.generate();
        if (identity == null) identity = DefaultHarnessIdentity.of("agent-anonymous");
        if (capabilities == null) capabilities = CapabilityView.of(java.util.Set.of());
        if (budget == null) budget = BudgetView.empty();
    }
}
