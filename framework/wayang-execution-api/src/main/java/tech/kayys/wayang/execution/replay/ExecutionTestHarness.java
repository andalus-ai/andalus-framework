package tech.kayys.wayang.execution.replay;

/**
 * Deterministic test harness executing test fixtures against replay environments.
 */
public interface ExecutionTestHarness {

    ReplayResult runTest(ExecutionFixture fixture);
}
