package tech.kayys.wayang.execution.core.economics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.economics.AdmissionContext;
import tech.kayys.wayang.execution.economics.AdmissionController;
import tech.kayys.wayang.execution.economics.AdmissionDecision;
import tech.kayys.wayang.execution.economics.BudgetDimension;
import tech.kayys.wayang.execution.economics.BudgetLimit;
import tech.kayys.wayang.execution.economics.Cost;
import tech.kayys.wayang.execution.economics.CostEstimate;
import tech.kayys.wayang.execution.economics.CostModel;
import tech.kayys.wayang.execution.economics.ReservationState;
import tech.kayys.wayang.execution.economics.ResourceAllocation;
import tech.kayys.wayang.execution.economics.ResourceBalance;
import tech.kayys.wayang.execution.economics.ResourceBroker;
import tech.kayys.wayang.execution.economics.ResourceBudget;
import tech.kayys.wayang.execution.economics.ResourceLedger;
import tech.kayys.wayang.execution.economics.ResourceReservation;
import tech.kayys.wayang.execution.economics.ResourceUsage;
import tech.kayys.wayang.execution.mobility.ExecutionIdentity;
import tech.kayys.wayang.execution.resource.model.ResourceScope;
import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionEconomicsTest {

    private ResourceLedger ledger;
    private AdmissionController admissionController;
    private CostModel costModel;
    private ResourceBroker broker;

    @BeforeEach
    void setUp() {
        ledger = new DefaultResourceLedger();
        admissionController = new DefaultAdmissionController();
        costModel = new DefaultCostModel(0.005, "USD");
        broker = new DefaultResourceBroker(ledger);
    }

    @Test
    void testResourceLedgerAccounting() {
        ExecutionIdentity execId = ExecutionIdentity.of("exec-econ-1", "wf-1", "agent-1");
        ResourceUsage usage1 = ResourceUsage.of(execId, ResourceType.COMPUTE, 150.0);
        ResourceUsage usage2 = ResourceUsage.of(execId, ResourceType.COMPUTE, 50.0);

        ledger.recordUsage(usage1);
        ledger.recordUsage(usage2);

        List<ResourceUsage> history = ledger.history(execId);
        assertEquals(2, history.size());

        ResourceBalance balance = ledger.balance(ResourceScope.EXECUTION, ResourceType.COMPUTE);
        assertEquals(200.0, balance.totalConsumed());
        assertTrue(balance.quotaRemaining() > 0);
        assertFalse(balance.isExhausted());
    }

    @Test
    void testAdmissionController() {
        ExecutionIdentity execId = ExecutionIdentity.of("exec-econ-2", "wf-2", "agent-2");

        // Normal admission with non-exhausted budget
        BudgetLimit limit = new BudgetLimit(BudgetDimension.DOLLARS, 100.0, 20.0);
        ResourceBudget budget = ResourceBudget.of("b-1", ResourceScope.SESSION, Map.of(BudgetDimension.DOLLARS, limit));
        AdmissionContext context = new AdmissionContext(execId, ResourceScope.SESSION, Set.of(), budget);

        AdmissionDecision decision = admissionController.evaluate(context);
        assertTrue(decision.admitted());
        assertTrue(decision.reservationId().isPresent());

        // Admission rejected with exhausted budget
        BudgetLimit exhaustedLimit = new BudgetLimit(BudgetDimension.DOLLARS, 100.0, 100.0);
        ResourceBudget exhaustedBudget = ResourceBudget.of("b-2", ResourceScope.SESSION, Map.of(BudgetDimension.DOLLARS, exhaustedLimit));
        AdmissionContext exhaustedContext = new AdmissionContext(execId, ResourceScope.SESSION, Set.of(), exhaustedBudget);

        AdmissionDecision rejectedDecision = admissionController.evaluate(exhaustedContext);
        assertFalse(rejectedDecision.admitted());
        assertTrue(rejectedDecision.reason().contains("Budget exhausted"));
    }

    @Test
    void testCostModel() {
        ExecutionIdentity execId = ExecutionIdentity.of("exec-econ-3", "wf-3", "agent-3");
        List<ResourceUsage> usages = List.of(
            ResourceUsage.of(execId, ResourceType.COMPUTE, 100.0),
            ResourceUsage.of(execId, ResourceType.COMPUTE, 200.0)
        );

        Cost cost = costModel.calculate(usages);
        assertEquals(1.5, cost.amount(), 0.001); // 300 units * 0.005
        assertEquals("USD", cost.currency());

        CostEstimate estimate = costModel.estimate(Set.of());
        assertTrue(estimate.minimumExpected() > 0.0);
        assertTrue(estimate.maximumExpected() >= estimate.minimumExpected());
    }

    @Test
    void testResourceBrokerReservationAllocationAndRelease() {
        ExecutionIdentity execId = ExecutionIdentity.of("exec-econ-4", "wf-4", "agent-4");

        ResourceReservation reservation = broker.reserve(execId, ResourceType.MEMORY, 512.0, Duration.ofMinutes(10));
        assertNotNull(reservation);
        assertEquals(ReservationState.RESERVED, reservation.state());

        Optional<ResourceAllocation> allocation = broker.allocate(reservation.id());
        assertTrue(allocation.isPresent());
        assertEquals(512.0, allocation.get().allocatedAmount());

        broker.release(reservation.id());
        // Verify ledger recorded allocation
        List<ResourceUsage> history = ledger.history(execId);
        assertEquals(1, history.size());
        assertEquals(512.0, history.get(0).unitsConsumed());
    }
}
