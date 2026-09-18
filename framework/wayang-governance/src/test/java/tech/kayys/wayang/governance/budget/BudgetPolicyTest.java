package tech.kayys.wayang.governance.budget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BudgetPolicyTest {

    private DefaultBudgetLedger ledger;
    private DefaultBudgetPolicy policy;

    @BeforeEach
    void setUp() {
        ledger = new DefaultBudgetLedger();
        policy = new DefaultBudgetPolicy(ledger);
        policy.setLimit(BudgetLimit.of(BudgetDimension.MODEL_COST, BudgetAmount.of(1.00, "USD")));
    }

    @Test
    void testAllowWithinBudget() {
        BudgetRequest req = BudgetRequest.of(BudgetDimension.MODEL_COST, BudgetAmount.of(0.30, "USD"));
        BudgetDecision decision = policy.evaluate(BudgetContext.of("exec-1"), req);

        assertTrue(decision.isAllowed());
        assertFalse(decision.isWarn());
        assertFalse(decision.isDenied());
        assertTrue(decision.remaining().isPresent());
        assertEquals(0, new BigDecimal("0.70").compareTo(decision.remaining().get().value()));
    }

    @Test
    void testWarnAtEightyPercent() {
        BudgetRequest req = BudgetRequest.of(BudgetDimension.MODEL_COST, BudgetAmount.of(0.85, "USD"));
        BudgetDecision decision = policy.evaluate(BudgetContext.of("exec-1"), req);

        assertTrue(decision.isAllowed());
        assertTrue(decision.isWarn());
        assertFalse(decision.isDenied());
    }

    @Test
    void testDenyExceedingLimit() {
        BudgetRequest req = BudgetRequest.of(BudgetDimension.MODEL_COST, BudgetAmount.of(1.50, "USD"));
        BudgetDecision decision = policy.evaluate(BudgetContext.of("exec-1"), req);

        assertTrue(decision.isDenied());
        assertFalse(decision.isAllowed());
        assertTrue(decision.reason().contains("Budget limit exceeded"));
    }
}
