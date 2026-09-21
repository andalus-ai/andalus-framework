package tech.kayys.andalus.execution.core.conformance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.conformance.CapabilityConstraint;
import tech.kayys.andalus.execution.conformance.CapabilityDescriptor;
import tech.kayys.andalus.execution.conformance.CapabilityId;
import tech.kayys.andalus.execution.conformance.CapabilityLevel;
import tech.kayys.andalus.execution.conformance.CapabilityRegistry;
import tech.kayys.andalus.execution.conformance.CapabilityRequirement;
import tech.kayys.andalus.execution.conformance.CapabilityResolution;
import tech.kayys.andalus.execution.conformance.CapabilityResolver;
import tech.kayys.andalus.execution.conformance.CapabilityVersion;
import tech.kayys.andalus.execution.conformance.ConformanceContext;
import tech.kayys.andalus.execution.conformance.ConformanceFinding;
import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.conformance.ConformanceReport;
import tech.kayys.andalus.execution.conformance.ConformanceResult;
import tech.kayys.andalus.execution.conformance.ConformanceStatus;
import tech.kayys.andalus.execution.conformance.ConformanceSuite;
import tech.kayys.andalus.execution.conformance.ConformanceTest;
import tech.kayys.andalus.execution.conformance.ConformanceVerifier;
import tech.kayys.andalus.execution.conformance.ContractId;
import tech.kayys.andalus.execution.conformance.ContractNegotiator;
import tech.kayys.andalus.execution.conformance.EvidenceReference;
import tech.kayys.andalus.execution.conformance.Guarantee;
import tech.kayys.andalus.execution.conformance.NegotiatedContract;
import tech.kayys.andalus.execution.conformance.ProfileId;
import tech.kayys.andalus.execution.conformance.TestId;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionConformanceTest {

    private CapabilityRegistry registry;
    private CapabilityResolver resolver;
    private ContractNegotiator negotiator;
    private ConformanceVerifier verifier;

    @BeforeEach
    void setUp() {
        registry = new DefaultCapabilityRegistry();
        resolver = new DefaultCapabilityResolver(registry);
        negotiator = new DefaultContractNegotiator();
        verifier = new DefaultConformanceVerifier();
    }

    @Test
    void testCapabilityRegistryAndResolver() {
        CapabilityId capId = CapabilityId.of("execution", "checkpoint.atomic");
        CapabilityDescriptor descriptor = new CapabilityDescriptor(
            capId,
            CapabilityVersion.of(1, 0, 0),
            CapabilityLevel.ADVANCED,
            Set.of(Guarantee.ATOMIC_COMMIT, Guarantee.DURABLE_STATE),
            Set.of(CapabilityConstraint.of("storage=persistent")),
            Set.of()
        );

        registry.register(descriptor);
        assertTrue(registry.resolve(capId).isPresent());
        assertEquals(1, registry.capabilities().size());

        CapabilityRequirement req = CapabilityRequirement.of(capId, CapabilityLevel.STANDARD, Set.of(Guarantee.ATOMIC_COMMIT));
        CapabilityResolution resolution = resolver.resolve(Set.of(req));
        assertTrue(resolution.satisfied());
        assertEquals(1, resolution.resolvedCapabilities().size());

        // Test unsatisfied requirement (higher level than available)
        CapabilityRequirement higherReq = CapabilityRequirement.of(capId, CapabilityLevel.FULL, Set.of());
        CapabilityResolution failedRes = resolver.resolve(Set.of(higherReq));
        assertFalse(failedRes.satisfied());
        assertEquals(1, failedRes.unsatisfiedRequirements().size());
    }

    @Test
    void testContractNegotiator() {
        CapabilityId capId = CapabilityId.of("execution", "replay.simulation");
        CapabilityDescriptor descriptor = new CapabilityDescriptor(
            capId,
            CapabilityVersion.of(1, 0, 0),
            CapabilityLevel.STANDARD,
            Set.of(Guarantee.REPLAYABLE, Guarantee.AUDITABLE),
            Set.of(),
            Set.of()
        );

        ConformanceProfile profile = ConformanceProfile.of(
            ProfileId.of("test-profile"),
            Set.of(descriptor),
            Set.of(ContractId.of("andalus", "replay", "1.0.0"))
        );

        CapabilityRequirement req = CapabilityRequirement.of(capId, CapabilityLevel.STANDARD, Set.of(Guarantee.REPLAYABLE));
        NegotiatedContract contract = negotiator.negotiate(Set.of(req), profile);
        assertTrue(contract.agreed());
        assertTrue(contract.agreedGuarantees().contains(Guarantee.REPLAYABLE));

        CapabilityRequirement missingReq = CapabilityRequirement.of(CapabilityId.of("andalus", "unknown.cap"), CapabilityLevel.BASIC);
        NegotiatedContract rejectedContract = negotiator.negotiate(Set.of(missingReq), profile);
        assertFalse(rejectedContract.agreed());
    }

    @Test
    void testConformanceVerifierWithSuites() {
        CapabilityId capId = CapabilityId.of("execution", "state.durable");
        CapabilityDescriptor descriptor = new CapabilityDescriptor(
            capId,
            CapabilityVersion.of(1, 0, 0),
            CapabilityLevel.STANDARD,
            Set.of(Guarantee.DURABLE_STATE),
            Set.of(),
            Set.of()
        );

        ContractId contractId = ContractId.of("andalus", "state-durable", "1.0.0");
        ConformanceProfile profile = ConformanceProfile.of(ProfileId.of("profile-1"), Set.of(descriptor), Set.of(contractId));

        ConformanceTest test1 = new ConformanceTest() {
            @Override public TestId id() { return TestId.of("test-durable-state-1"); }
            @Override public ContractId targetContract() { return contractId; }
            @Override public ConformanceResult execute(ConformanceContext context) {
                return ConformanceResult.pass(id(), contractId, EvidenceReference.of("ev-1", "STATE_HASH"));
            }
        };

        ConformanceTest test2 = new ConformanceTest() {
            @Override public TestId id() { return TestId.of("test-durable-state-2"); }
            @Override public ContractId targetContract() { return contractId; }
            @Override public ConformanceResult execute(ConformanceContext context) {
                return ConformanceResult.inconclusive(id(), contractId, "Network degraded during check");
            }
        };

        ConformanceSuite suite = new ConformanceSuite() {
            @Override public ContractId contract() { return contractId; }
            @Override public List<ConformanceTest> tests() { return List.of(test1, test2); }
        };

        ConformanceReport report = verifier.verify(profile, List.of(suite));
        assertNotNull(report);
        assertEquals(2, report.summary().totalTests());
        assertEquals(1, report.summary().passed());
        assertEquals(1, report.summary().inconclusive());
        assertEquals(0, report.summary().failed());
        assertTrue(report.isConformant());
    }
}
