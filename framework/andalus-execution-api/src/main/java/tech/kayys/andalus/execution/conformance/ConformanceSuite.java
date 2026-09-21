package tech.kayys.andalus.execution.conformance;

import java.util.List;

/**
 * Suite of conformance tests associated with a formal execution contract.
 */
public interface ConformanceSuite {

    ContractId contract();

    List<ConformanceTest> tests();
}
