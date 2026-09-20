package tech.kayys.wayang.execution.conformance;

import java.util.Objects;

/**
 * Identifier for a conformance test.
 */
public record TestId(String value) {

    public TestId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static TestId of(String value) {
        return new TestId(value);
    }
}
