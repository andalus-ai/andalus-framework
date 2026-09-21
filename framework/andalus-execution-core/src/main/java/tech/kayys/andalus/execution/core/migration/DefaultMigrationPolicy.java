package tech.kayys.andalus.execution.core.migration;

import tech.kayys.andalus.execution.migration.MigrationDecision;
import tech.kayys.andalus.execution.migration.MigrationPlan;
import tech.kayys.andalus.execution.migration.MigrationPolicy;

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
