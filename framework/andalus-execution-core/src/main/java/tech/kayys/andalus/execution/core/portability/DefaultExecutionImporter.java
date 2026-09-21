package tech.kayys.andalus.execution.core.portability;

import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.portability.CompatibilityReport;
import tech.kayys.andalus.execution.portability.ExecutionImporter;
import tech.kayys.andalus.execution.portability.ImportAssessment;
import tech.kayys.andalus.execution.portability.ImportResult;
import tech.kayys.andalus.execution.portability.PortabilityVerifier;
import tech.kayys.andalus.execution.portability.PortableExecution;

import java.util.Objects;

/**
 * Standard execution importer assessing and hydrating portable executions.
 */
public class DefaultExecutionImporter implements ExecutionImporter {

    private final PortabilityVerifier verifier;

    public DefaultExecutionImporter(PortabilityVerifier verifier) {
        this.verifier = Objects.requireNonNull(verifier, "verifier cannot be null");
    }

    public DefaultExecutionImporter() {
        this(new DefaultPortabilityVerifier());
    }

    @Override
    public ImportAssessment assess(PortableExecution execution, ConformanceProfile targetProfile) {
        Objects.requireNonNull(execution, "execution cannot be null");
        Objects.requireNonNull(targetProfile, "targetProfile cannot be null");

        CompatibilityReport report = verifier.verifyCompatibility(execution, targetProfile);
        return new ImportAssessment(report.compatible(), report, targetProfile.id().value());
    }

    @Override
    public ImportResult importExecution(PortableExecution execution, ConformanceProfile targetProfile) {
        ImportAssessment assessment = assess(execution, targetProfile);
        if (!assessment.acceptable()) {
            return ImportResult.failure(execution.manifest().executionId(), "Incompatible execution: " + assessment.compatibility().issues());
        }
        return ImportResult.success(execution.manifest().executionId());
    }
}
