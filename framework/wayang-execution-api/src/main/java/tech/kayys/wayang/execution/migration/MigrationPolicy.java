package tech.kayys.wayang.execution.migration;

public interface MigrationPolicy {
    MigrationDecision evaluate(MigrationPlan plan);
}
