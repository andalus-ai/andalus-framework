package tech.kayys.andalus.execution.governance;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class ToolBudgetReservationTest {

    @Test
    void atomicallyReservesNoMoreThanConfiguredCallLimit() throws Exception {
        int contenders = 64;
        ToolBudget budget = new ToolBudget("tenant-a", "user-a", 7, -1.0, -1);
        ExecutorService pool = Executors.newFixedThreadPool(contenders);
        CountDownLatch ready = new CountDownLatch(contenders);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<Boolean>> attempts = new ArrayList<>();
            for (int i = 0; i < contenders; i++) {
                attempts.add(pool.submit(() -> {
                    ready.countDown();
                    start.await();
                    return budget.tryAcquireCall();
                }));
            }
            ready.await();
            start.countDown();

            long admitted = 0;
            for (Future<Boolean> attempt : attempts) {
                if (attempt.get()) admitted++;
            }

            assertEquals(7, admitted);
            assertEquals(7, budget.usedCalls());
            assertFalse(budget.tryAcquireCall());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void measuredUsageExhaustsDurationAndCostForSubsequentCalls() {
        ToolBudget budget = new ToolBudget("tenant-a", "user-a", -1, 1.0, 1_000);

        assertTrue(budget.tryAcquireCall());
        budget.recordUsage(600, 0.6);
        assertTrue(budget.tryAcquireCall());
        budget.recordUsage(400, 0.4);

        assertTrue(budget.isExhausted());
        assertFalse(budget.tryAcquireCall());
        assertEquals(1_000, budget.usedDurationMs());
        assertEquals(1.0, budget.usedCostUsd(), 0.000001);
    }

    @Test
    void rejectsNegativeMeasuredUsage() {
        ToolBudget budget = ToolBudget.unlimited();
        assertThrows(IllegalArgumentException.class, () -> budget.recordUsage(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> budget.recordUsage(0, -0.1));
    }
}
