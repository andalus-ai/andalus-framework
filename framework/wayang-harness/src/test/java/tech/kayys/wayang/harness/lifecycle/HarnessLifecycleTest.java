package tech.kayys.wayang.harness.lifecycle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HarnessLifecycleTest {

    @Test
    void standardExecutionLifecycleTransitions() {
        DefaultHarnessLifecycle lifecycle = new DefaultHarnessLifecycle();
        assertEquals(HarnessExecutionStatus.CREATED, lifecycle.status());
        assertFalse(lifecycle.isTerminal());

        lifecycle.transitionTo(HarnessExecutionStatus.ADMITTED);
        assertEquals(HarnessExecutionStatus.ADMITTED, lifecycle.status());

        lifecycle.transitionTo(HarnessExecutionStatus.INITIALIZING);
        assertEquals(HarnessExecutionStatus.INITIALIZING, lifecycle.status());

        lifecycle.transitionTo(HarnessExecutionStatus.RUNNING);
        assertEquals(HarnessExecutionStatus.RUNNING, lifecycle.status());

        lifecycle.transitionTo(HarnessExecutionStatus.COMPLETED);
        assertEquals(HarnessExecutionStatus.COMPLETED, lifecycle.status());
        assertTrue(lifecycle.isTerminal());
    }

    @Test
    void rejectsIllegalTransitions() {
        DefaultHarnessLifecycle lifecycle = new DefaultHarnessLifecycle();
        // Cannot jump directly from CREATED to RUNNING
        assertThrows(IllegalStateException.class, () -> lifecycle.transitionTo(HarnessExecutionStatus.RUNNING));

        lifecycle.transitionTo(HarnessExecutionStatus.ADMITTED);
        // Cannot jump directly from ADMITTED to COMPLETED
        assertThrows(IllegalStateException.class, () -> lifecycle.transitionTo(HarnessExecutionStatus.COMPLETED));
    }

    @Test
    void terminalStatesRejectFurtherTransitions() {
        DefaultHarnessLifecycle lifecycle = new DefaultHarnessLifecycle(HarnessExecutionStatus.COMPLETED);
        assertTrue(lifecycle.isTerminal());

        assertThrows(IllegalStateException.class, () -> lifecycle.transitionTo(HarnessExecutionStatus.RUNNING));
        assertThrows(IllegalStateException.class, () -> lifecycle.transitionTo(HarnessExecutionStatus.FAILED));
    }

    @Test
    void suspensionAndResumptionFlow() {
        DefaultHarnessLifecycle lifecycle = new DefaultHarnessLifecycle(HarnessExecutionStatus.RUNNING);

        lifecycle.suspend();
        assertEquals(HarnessExecutionStatus.SUSPENDED, lifecycle.status());

        lifecycle.resume();
        assertEquals(HarnessExecutionStatus.RESUMING, lifecycle.status());

        lifecycle.transitionTo(HarnessExecutionStatus.RUNNING);
        assertEquals(HarnessExecutionStatus.RUNNING, lifecycle.status());
    }

    @Test
    void cancellationMarksTerminal() {
        DefaultHarnessLifecycle lifecycle = new DefaultHarnessLifecycle(HarnessExecutionStatus.RUNNING);
        lifecycle.cancel();

        assertEquals(HarnessExecutionStatus.CANCELLED, lifecycle.status());
        assertTrue(lifecycle.isTerminal());
    }
}
