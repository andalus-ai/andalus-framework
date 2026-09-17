package tech.kayys.wayang.harness.governance.budget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BudgetLedgerTest {

    private DefaultBudgetLedger ledger;

    @BeforeEach
    void setUp() {
        ledger = new DefaultBudgetLedger();
    }

    @Test
    void testRecordConsumption() {
        BudgetConsumption c1 = new BudgetConsumption("exec-1", "act-1", BudgetDimension.MODEL_INPUT_TOKENS, BudgetAmount.of(1500, "tokens"), Instant.now(), "llm");
        BudgetConsumption c2 = new BudgetConsumption("exec-1", "act-2", BudgetDimension.MODEL_INPUT_TOKENS, BudgetAmount.of(500, "tokens"), Instant.now(), "llm");

        ledger.record(c1);
        ledger.record(c2);

        BudgetUsage usage = ledger.usage(BudgetDimension.MODEL_INPUT_TOKENS);
        assertEquals(new BigDecimal("2000"), usage.consumed().value());
        assertEquals(BigDecimal.ZERO, usage.reserved().value());
        assertEquals(new BigDecimal("2000"), usage.totalAllocated().value());
    }

    @Test
    void testReservationTracking() {
        ledger.addReservation(BudgetDimension.MODEL_COST, BudgetAmount.of(0.50, "USD"));
        BudgetUsage usage = ledger.usage(BudgetDimension.MODEL_COST);
        assertEquals(new BigDecimal("0.5"), usage.reserved().value());
        assertEquals(new BigDecimal("0.5"), usage.totalAllocated().value());

        ledger.removeReservation(BudgetDimension.MODEL_COST, BudgetAmount.of(0.20, "USD"));
        BudgetUsage usageAfter = ledger.usage(BudgetDimension.MODEL_COST);
        assertEquals(new BigDecimal("0.3"), usageAfter.reserved().value());
    }

    @Test
    void testSnapshot() {
        ledger.setLimit(BudgetLimit.of(BudgetDimension.TOOL_CALLS, BudgetAmount.of(100, "calls")));
        ledger.record(new BudgetConsumption("exec-1", "act-1", BudgetDimension.TOOL_CALLS, BudgetAmount.of(5, "calls"), Instant.now(), "tool"));

        BudgetSnapshot snap = ledger.snapshot();
        assertNotNull(snap);
        assertTrue(snap.limits().containsKey(BudgetDimension.TOOL_CALLS));
        assertTrue(snap.usages().containsKey(BudgetDimension.TOOL_CALLS));
        assertEquals(new BigDecimal("5"), snap.usages().get(BudgetDimension.TOOL_CALLS).consumed().value());
    }
}
