package tech.kayys.wayang.harness.isolation;

import java.util.Objects;

/**
 * Numeric amount and unit representing a resource quota or constraint.
 */
public record Quantity(double amount, String unit) {

    public Quantity {
        Objects.requireNonNull(unit, "Unit cannot be null");
    }

    public static Quantity of(double amount, String unit) {
        return new Quantity(amount, unit);
    }

    public static Quantity cores(double count) {
        return new Quantity(count, "cores");
    }

    public static Quantity megabytes(double mb) {
        return new Quantity(mb, "MB");
    }

    public static Quantity seconds(double sec) {
        return new Quantity(sec, "seconds");
    }
}
