package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.context.ContextSnapshot;
import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.execution.state.ExecutionId;

/**
 * Defines the contract for agent turn context operations in the Wayang framework.
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
