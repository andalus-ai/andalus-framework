package tech.kayys.andalus.execution.core.portability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.conformance.CapabilityDescriptor;
import tech.kayys.andalus.execution.conformance.CapabilityId;
import tech.kayys.andalus.execution.conformance.CapabilityLevel;
import tech.kayys.andalus.execution.conformance.CapabilityVersion;
import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.conformance.ContractId;
import tech.kayys.andalus.execution.conformance.ProfileId;
import tech.kayys.andalus.execution.mobility.ExecutionCursor;
import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.portability.CompatibilityReport;
import tech.kayys.andalus.execution.portability.ExecutionExporter;
import tech.kayys.andalus.execution.portability.ExecutionImporter;
import tech.kayys.andalus.execution.portability.ExecutionPackage;
import tech.kayys.andalus.execution.portability.ImportAssessment;
import tech.kayys.andalus.execution.portability.ImportResult;
import tech.kayys.andalus.execution.portability.PortableExecution;
import tech.kayys.andalus.execution.portability.PortabilityVerifier;
import tech.kayys.andalus.execution.provenance.ProvenanceChain;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionPortabilityTest {

    private ExecutionExporter exporter;
    private PortabilityVerifier verifier;
    private ExecutionImporter importer;

    @BeforeEach
    void setUp() {
        exporter = new DefaultExecutionExporter();
        verifier = new DefaultPortabilityVerifier();
        importer = new DefaultExecutionImporter(verifier);
    }

    @Test
    void testExportPackageAndVerifyIntegrity() {
        ExecutionIdentity execId = ExecutionIdentity.of("exec-101", "wf-1", "agent-1");
        ExecutionDurableState state = ExecutionDurableState.of(execId, ExecutionCursor.of("wf-1", "step-1", 1, "RUNNING"));
        ProvenanceChain provenance = new ProvenanceChain(execId.id(), List.of(), "digest-sha256-chain");

        PortableExecution portable = exporter.export(execId, state, provenance);
        assertNotNull(portable);
        assertNotNull(portable.manifest());
        assertEquals("exec-101", portable.manifest().executionId().id());

        ExecutionPackage pkg = new ExecutionPackage(portable);
        assertTrue(pkg.verifyIntegrity());
    }

    @Test
    void testPortabilityVerificationAndImport() {
        ExecutionIdentity execId = ExecutionIdentity.of("exec-202", "wf-2", "agent-2");
        ExecutionDurableState state = ExecutionDurableState.of(execId, ExecutionCursor.of("wf-2", "step-2", 1, "RUNNING"));
        ProvenanceChain provenance = new ProvenanceChain(execId.id(), List.of(), "digest-chain");

        PortableExecution portable = exporter.export(execId, state, provenance);

        ConformanceProfile targetProfile = ConformanceProfile.of(
            ProfileId.of("target-runtime-linux"),
            Set.of(CapabilityDescriptor.of(CapabilityId.of("andalus", "standard"), CapabilityVersion.of(1, 0, 0), CapabilityLevel.STANDARD)),
            Set.of(ContractId.of("andalus", "execution", "1.0.0"))
        );

        CompatibilityReport report = verifier.verifyCompatibility(portable, targetProfile);
        assertTrue(report.compatible());

        ImportAssessment assessment = importer.assess(portable, targetProfile);
        assertTrue(assessment.acceptable());

        ImportResult result = importer.importExecution(portable, targetProfile);
        assertTrue(result.success());
        assertEquals(execId, result.executionId());
    }
}
