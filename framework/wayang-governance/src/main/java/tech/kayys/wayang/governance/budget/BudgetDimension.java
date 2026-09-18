package tech.kayys.wayang.governance.budget;

import java.util.Objects;

/**
 * Metric dimension tracked by the Harness budget engine.
 */
public record BudgetDimension(
        String id,
        String unit
) {
    public BudgetDimension {
        Objects.requireNonNull(id, "id");
        unit = unit == null ? "units" : unit;
    }

    public static final BudgetDimension MODEL_INPUT_TOKENS = of("model.input_tokens", "tokens");
    public static final BudgetDimension MODEL_OUTPUT_TOKENS = of("model.output_tokens", "tokens");
    public static final BudgetDimension MODEL_COST = of("model.cost", "USD");
    public static final BudgetDimension TOOL_CALLS = of("tool.calls", "calls");
    public static final BudgetDimension EXECUTION_TIME = of("execution.time", "seconds");

    public static BudgetDimension of(String id, String unit) {
        return new BudgetDimension(id, unit);
    }
}
