package tech.kayys.wayang.harness.plan;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable value record implementing {@link ExecutionPlan}.
 */
public record DefaultExecutionPlan(
        String id,
        List<ExecutionStep> steps
) implements ExecutionPlan {

    public DefaultExecutionPlan {
        id = id == null ? "plan-" + UUID.randomUUID() : id;
        steps = steps == null ? List.of() : List.copyOf(steps);
    }

    public static DefaultExecutionPlan of(List<ExecutionStep> steps) {
        return new DefaultExecutionPlan(null, steps);
    }

    public static DefaultExecutionPlan of(String id, List<ExecutionStep> steps) {
        return new DefaultExecutionPlan(id, steps);
    }
}
