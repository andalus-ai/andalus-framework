package tech.kayys.wayang.harness.governance.budget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BudgetManagerTest {

    private DefaultBudgetLedger ledger;
    private DefaultBudgetPolicy policy;
    private DefaultBudgetManager manager;

    @BeforeEach
    void setUp() {
        ledger = new DefaultBudgetLedger();
        policy = new DefaultBudgetPolicy(ledger);
        policy.setLimit(BudgetLimit.of(BudgetDimension.MODEL_COST, BudgetAmount.of(2.00, "USD")));
        manager = new DefaultBudgetManager(ledger, policy);
    }

    @Test
    void testReserveAndSettle() {
        BudgetRequest req = BudgetRequest.of(BudgetDimension.MODEL_COST, BudgetAmount.of(0.50, "USD"));
        BudgetReservation resv = manager.reserve(req);

        assertNotNull(resv);
        assertTrue(resv.active());
        assertEquals(0, new BigDecimal("0.50").compareTo(ledger.usage(BudgetDimension.MODEL_COST).reserved().value()));

        // Settle with actual amount of 0.42
        manager.settle(resv, BudgetAmount.of(0.42, "USD"));
        assertFalse(resv.active());
        assertEquals(0, BigDecimal.ZERO.compareTo(ledger.usage(BudgetDimension.MODEL_COST).reserved().value()));
        assertEquals(0, new BigDecimal("0.42").compareTo(ledger.usage(BudgetDimension.MODEL_COST).consumed().value()));
    }

    @Test
    void testReserveAndRelease() {
        BudgetRequest req = BudgetRequest.of(BudgetDimension.MODEL_COST, BudgetAmount.of(0.80, "USD"));
        BudgetReservation resv = manager.reserve(req);
        assertTrue(resv.active());

        // Cancel / release
        manager.release(resv);
        assertFalse(resv.active());
        assertEquals(0, BigDecimal.ZERO.compareTo(ledger.usage(BudgetDimension.MODEL_COST).reserved().value()));
        assertEquals(0, BigDecimal.ZERO.compareTo(ledger.usage(BudgetDimension.MODEL_COST).consumed().value()));
    }
}
