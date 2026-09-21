package tech.kayys.andalus.execution.core.conformance;

import tech.kayys.andalus.execution.conformance.ConformanceContext;
import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.conformance.ConformanceReport;
import tech.kayys.andalus.execution.conformance.ConformanceResult;
import tech.kayys.andalus.execution.conformance.ConformanceStatus;
import tech.kayys.andalus.execution.conformance.ConformanceSuite;
import tech.kayys.andalus.execution.conformance.ConformanceSummary;
import tech.kayys.andalus.execution.conformance.ConformanceTest;
import tech.kayys.andalus.execution.conformance.ConformanceVerifier;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Conformance verifier executing suites against a profile and producing formal reports.
 */
public class DefaultConformanceVerifier implements ConformanceVerifier {

    @Override
    public ConformanceReport verify(ConformanceProfile profile, Collection<ConformanceSuite> suites) {
        Objects.requireNonNull(profile, "profile cannot be null");
        ConformanceContext context = ConformanceContext.of(profile);

        Set<ConformanceResult> results = new HashSet<>();
        int passed = 0;
        int failed = 0;
        int inconclusive = 0;
        int notApplicable = 0;

        if (suites != null) {
            for (ConformanceSuite suite : suites) {
                if (suite.tests() != null) {
                    for (ConformanceTest test : suite.tests()) {
                        ConformanceResult res;
                        try {
                            res = test.execute(context);
                        } catch (Exception e) {
                            res = ConformanceResult.inconclusive(test.id(), suite.contract(), "Test execution threw exception: " + e.getMessage());
                        }

                        results.add(res);
                        switch (res.status()) {
                            case PASS -> passed++;
                            case FAIL -> failed++;
                            case INCONCLUSIVE -> inconclusive++;
                            case NOT_APPLICABLE -> notApplicable++;
                        }
                    }
                }
            }
        }

        ConformanceSummary summary = new ConformanceSummary(results.size(), passed, failed, inconclusive, notApplicable);
        return new ConformanceReport(profile.id(), Instant.now(), results, summary);
    }
}
