package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.context.ContextSnapshot;
import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.execution.state.ExecutionId;

public interface AgentTurnContext {

    ExecutionId executionId();

    HarnessIdentity identity();

    ContextSnapshot context();

    CapabilityView capabilities();

    BudgetView budget();

    ToolCatalogView tools();

    ModelCatalogView models();
}
