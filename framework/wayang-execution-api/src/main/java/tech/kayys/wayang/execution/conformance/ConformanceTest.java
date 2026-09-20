package tech.kayys.wayang.execution.conformance;

/**
 * Deterministic test evaluating whether a component conforms to a specific contract.
 */
public interface ConformanceTest {

    TestId id();

    ContractId targetContract();

    ConformanceResult execute(ConformanceContext context);
}
