package tech.kayys.wayang.workflow.graph;

import java.util.Map;
import java.util.Objects;

/**
 * Expression evaluated dynamically against workflow context.
 */
public record ConditionExpression(
        String variable,
        String operator,
        Object expectedValue
) {

    public ConditionExpression {
        Objects.requireNonNull(variable, "variable cannot be null");
        operator = operator != null ? operator : "equals";
    }

    public static ConditionExpression equals(String variable, Object expectedValue) {
        return new ConditionExpression(variable, "equals", expectedValue);
    }

    public static ConditionExpression notEquals(String variable, Object expectedValue) {
        return new ConditionExpression(variable, "not_equals", expectedValue);
    }

    public boolean evaluate(Map<String, Object> context) {
        if (context == null) return false;
        Object actual = context.get(variable);
        return switch (operator) {
            case "equals" -> Objects.equals(actual, expectedValue);
            case "not_equals" -> !Objects.equals(actual, expectedValue);
            case "exists" -> actual != null;
            case "not_exists" -> actual == null;
            default -> false;
        };
    }
}
