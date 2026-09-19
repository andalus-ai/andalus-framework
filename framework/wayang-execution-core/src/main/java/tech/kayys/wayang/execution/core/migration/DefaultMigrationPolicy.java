package tech.kayys.wayang.execution.core.migration;

import tech.kayys.wayang.execution.migration.MigrationDecision;
import tech.kayys.wayang.execution.migration.MigrationPlan;
import tech.kayys.wayang.execution.migration.MigrationPolicy;

public class DefaultMigrationPolicy implements MigrationPolicy {

    @Override
    public MigrationDecision evaluate(MigrationPlan plan) {
        if (plan.checkpointId() == null || plan.checkpointId().isBlank()) {
            return MigrationDecision.DENIED;
        }
        if (plan.sourceWorkerId().equalsIgnoreCase(plan.targetWorkerId())) {
            return MigrationDecision.DENIED;
        }
        return MigrationDecision.ALLOWED;
    }
}
