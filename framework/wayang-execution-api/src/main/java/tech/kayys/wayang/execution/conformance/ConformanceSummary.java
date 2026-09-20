package tech.kayys.wayang.execution.conformance;

/**
 * Aggregated summary of test outcomes.
 */
public record ConformanceSummary(
    int totalTests,
    int passed,
    int failed,
    int inconclusive,
    int notApplicable
) {
    public boolean isAllPassed() {
        return failed == 0 && totalTests > 0;
    }
}
