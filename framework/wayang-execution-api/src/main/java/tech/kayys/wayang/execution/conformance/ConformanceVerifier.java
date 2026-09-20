package tech.kayys.wayang.execution.conformance;

import java.util.Collection;

/**
 * Conformance verification engine executing test suites against claimed profiles.
 */
public interface ConformanceVerifier {

    ConformanceReport verify(ConformanceProfile profile, Collection<ConformanceSuite> suites);
}
