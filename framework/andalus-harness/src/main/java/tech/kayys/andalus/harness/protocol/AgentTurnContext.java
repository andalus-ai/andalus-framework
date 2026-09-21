package tech.kayys.andalus.harness.protocol;

import tech.kayys.andalus.harness.context.ContextSnapshot;
import tech.kayys.andalus.harness.context.HarnessIdentity;
import tech.kayys.andalus.harness.execution.state.ExecutionId;

/**
 * Defines the contract for agent turn context operations in the Andalus framework.
 */


public interface AgentTurnContext {

    ExecutionId executionId();

    HarnessIdentity identity();

    ContextSnapshot context();

    CapabilityView capabilities();

    BudgetView budget();

    ToolCatalogView tools();

    ModelCatalogView models();
}
