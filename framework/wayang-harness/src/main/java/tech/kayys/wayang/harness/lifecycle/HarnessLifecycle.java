package tech.kayys.wayang.harness.lifecycle;

/**
 * Manages lifecycle states and transitions for an ongoing agent execution.
 */
public interface HarnessLifecycle {

    HarnessExecutionStatus status();

    void transitionTo(HarnessExecutionStatus status);

    boolean isTerminal();

    void cancel();

    void suspend();

    void resume();
}
