package tech.kayys.andalus.execution.conformance;

import java.util.List;
import java.util.Objects;

/**
 * Result of executing an individual conformance test.
 */
public record ConformanceResult(
    TestId test,
    ContractId contract,
    ConformanceStatus status,
    EvidenceReference evidence,
    List<ConformanceFinding> findings
) {
    public ConformanceResult {
        Objects.requireNonNull(test, "test cannot be null");
        Objects.requireNonNull(contract, "contract cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        findings = findings != null ? List.copyOf(findings) : List.of();
    }

    public static ConformanceResult pass(TestId test, ContractId contract, EvidenceReference evidence) {
        return new ConformanceResult(test, contract, ConformanceStatus.PASS, evidence, List.of());
    }

    public static ConformanceResult fail(TestId test, ContractId contract, List<ConformanceFinding> findings) {
        return new ConformanceResult(test, contract, ConformanceStatus.FAIL, null, findings);
    }

    public static ConformanceResult inconclusive(TestId test, ContractId contract, String reason) {
        return new ConformanceResult(test, contract, ConformanceStatus.INCONCLUSIVE, null,
            List.of(new ConformanceFinding(FindingType.ENVIRONMENT_LIMITATION, reason, Severity.WARNING, null)));
    }
}
