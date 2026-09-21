package tech.kayys.andalus.execution.migration;

public interface MigrationPolicy {
    MigrationDecision evaluate(MigrationPlan plan);
}
