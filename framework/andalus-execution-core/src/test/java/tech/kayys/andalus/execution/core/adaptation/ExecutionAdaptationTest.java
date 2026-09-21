package tech.kayys.andalus.execution.core.adaptation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.adaptation.AdaptationAssessment;
import tech.kayys.andalus.execution.adaptation.AdaptationAuthorizer;
import tech.kayys.andalus.execution.adaptation.AdaptationEngine;
import tech.kayys.andalus.execution.adaptation.AdaptationPlan;
import tech.kayys.andalus.execution.adaptation.AdaptationType;
import tech.kayys.andalus.execution.adaptation.AuthorizationDecision;
import tech.kayys.andalus.execution.adaptation.ExecutionSignal;
import tech.kayys.andalus.execution.adaptation.RiskAssessment;
import tech.kayys.andalus.execution.adaptation.RiskLevel;
import tech.kayys.andalus.execution.adaptation.SignalSeverity;
import tech.kayys.andalus.execution.adaptation.SignalType;
import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.conformance.ProfileId;
import tech.kayys.andalus.execution.fabric.FabricSnapshot;
import tech.kayys.andalus.execution.fabric.NodeDescriptor;
import tech.kayys.andalus.execution.fabric.NodeIdentity;
import tech.kayys.andalus.execution.fabric.NodeStatus;
import tech.kayys.andalus.execution.fabric.TopologyDescriptor;
import tech.kayys.andalus.execution.fabric.AndalusNode;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.portability.RuntimeIdentity;
import tech.kayys.andalus.execution.resource.requirement.ResourceRequirement;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionAdaptationTest {

    private AdaptationEngine engine;
    private AdaptationAuthorizer authorizer;

    @BeforeEach
    void setUp() {
        engine = new DefaultAdaptationEngine();
        authorizer = new DefaultAdaptationAuthorizer();
    }

    @Test
    void testResourcePressureAssessmentAndFormulation() {
        ExecutionIdentity execId = ExecutionIdentity.of("exec-adapt-1", "wf-1", "agent-1");
        ExecutionSignal signal = ExecutionSignal.of("sig-1", SignalType.RESOURCE_PRESSURE, SignalSeverity.HIGH, execId);

        FabricSnapshot fabric = new FabricSnapshot("fabric-1", Instant.now(), List.of(), java.util.Map.of());

        AdaptationAssessment assessment = engine.assess(signal, fabric);
        assertTrue(assessment.adaptationRequired());
        assertTrue(assessment.feasibleActions().contains(AdaptationType.RESOURCE_RESIZE));

        Optional<AdaptationPlan> plan = engine.plan(signal, null, fabric);
        assertTrue(plan.isPresent());
        assertEquals(AdaptationType.RESOURCE_RESIZE, plan.get().type());
        assertEquals(execId, plan.get().executionId());
        assertEquals(RiskLevel.LOW, plan.get().risk().risk());

        ConformanceProfile profile = ConformanceProfile.of(ProfileId.of("prof"), Set.of(), Set.of());
        AuthorizationDecision decision = authorizer.authorize(plan.get(), execId, profile);
        assertTrue(decision.authorized());
    }

    @Test
    void testAuthorizerRejectsUnacceptableOrUnauthorizedRisk() {
        ExecutionIdentity execId = ExecutionIdentity.of("exec-adapt-2", "wf-2", "agent-2");
        AdaptationPlan unacceptablePlan = AdaptationPlan.of(
            "plan-unacc",
            execId,
            AdaptationType.FAILOVER,
            List.of(),
            RiskAssessment.unacceptable("Too risky without fallback")
        );

        ConformanceProfile profile = ConformanceProfile.of(ProfileId.of("prof"), Set.of(), Set.of());
        AuthorizationDecision decision1 = authorizer.authorize(unacceptablePlan, execId, profile);
        assertFalse(decision1.authorized());

        AdaptationPlan humanApprovalPlan = AdaptationPlan.of(
            "plan-human",
            execId,
            AdaptationType.NODE_MIGRATION,
            List.of(),
            RiskAssessment.high("Must be approved by operator")
        );
        AuthorizationDecision decision2 = authorizer.authorize(humanApprovalPlan, execId, profile);
        assertFalse(decision2.authorized());
        assertTrue(decision2.reason().contains("Requires human approval"));
    }
}
