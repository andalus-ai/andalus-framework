package tech.kayys.andalus.harness.execution.state;

import tech.kayys.andalus.harness.context.HarnessContext;

/**
 * First-class durable state representation of an agent execution.
 */
public interface ExecutionState {

    ExecutionId executionId();

    ExecutionStatus status();

    long version();

    HarnessContext context();

    ExecutionCursor cursor();

    ExecutionData data();
}
