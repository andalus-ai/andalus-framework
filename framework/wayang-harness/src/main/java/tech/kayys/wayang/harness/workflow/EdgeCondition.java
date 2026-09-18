package tech.kayys.wayang.harness.workflow;

import java.util.Objects;

/**
 * Predicate condition governing edge traversal or activation.
 */
public record EdgeCondition(String expression, String expectedValue) {

    public EdgeCondition {
        expression = expression != null ? expression : "true";
        expectedValue = expectedValue != null ? expectedValue : "true";
    }

    public static EdgeCondition alwaysTrue() {
        return new EdgeCondition("true", "true");
    }

    public static EdgeCondition whenEquals(String expression, String value) {
        return new EdgeCondition(expression, value);
    }

    public boolean matches(String actualValue) {
        if ("true".equals(expression) && "true".equals(expectedValue)) {
            return true;
        }
        return Objects.equals(expectedValue, actualValue);
    }
}
