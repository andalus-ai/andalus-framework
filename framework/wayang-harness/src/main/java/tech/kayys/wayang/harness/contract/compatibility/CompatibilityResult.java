package tech.kayys.wayang.harness.contract.compatibility;

import java.util.List;

/**
 * Outcome of evaluating multidimensional contract compatibility.
 */
public record CompatibilityResult(
        boolean compatible,
        List<String> violations
) {
    public CompatibilityResult {
        violations = violations != null ? List.copyOf(violations) : List.of();
    }

    public static CompatibilityResult success() {
        return new CompatibilityResult(true, List.of());
    }

    public static CompatibilityResult failure(List<String> reasons) {
        return new CompatibilityResult(false, reasons);
    }
}
