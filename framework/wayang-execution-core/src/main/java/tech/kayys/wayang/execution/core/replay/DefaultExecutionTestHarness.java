package tech.kayys.wayang.execution.core.replay;

import tech.kayys.wayang.execution.replay.ExecutionFixture;
import tech.kayys.wayang.execution.replay.ExecutionTestHarness;
import tech.kayys.wayang.execution.replay.ReplayEngine;
import tech.kayys.wayang.execution.replay.ReplayEnvironment;
import tech.kayys.wayang.execution.replay.ReplayResult;

import java.util.Objects;

/**
 * Standard implementation of {@link ExecutionTestHarness}.
 */
public class DefaultExecutionTestHarness implements ExecutionTestHarness {

    private final ReplayEngine replayEngine;
    private final ReplayEnvironment environment;

    public DefaultExecutionTestHarness(ReplayEngine replayEngine, ReplayEnvironment environment) {
        this.replayEngine = Objects.requireNonNull(replayEngine, "replayEngine cannot be null");
        this.environment = Objects.requireNonNull(environment, "environment cannot be null");
    }

    public DefaultExecutionTestHarness() {
        this(new DefaultReplayEngine(), DefaultReplayEnvironment.standard("test-env"));
    }

    @Override
    public ReplayResult runTest(ExecutionFixture fixture) {
        Objects.requireNonNull(fixture, "fixture cannot be null");
        return replayEngine.replay(fixture.manifest(), environment);
    }
}
