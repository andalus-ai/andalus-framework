package tech.kayys.wayang.execution.core.replay;

import tech.kayys.wayang.execution.replay.ReplayAuthority;
import tech.kayys.wayang.execution.replay.ReplayDecision;
import tech.kayys.wayang.execution.replay.ReplayEngine;
import tech.kayys.wayang.execution.replay.ReplayEnvironment;
import tech.kayys.wayang.execution.replay.ReplayManifest;
import tech.kayys.wayang.execution.replay.ReplayResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Standard implementation of {@link ReplayEngine}.
 */
public class DefaultReplayEngine implements ReplayEngine {

    private final ReplayAuthority authority;

    public DefaultReplayEngine(ReplayAuthority authority) {
        this.authority = Objects.requireNonNull(authority, "authority cannot be null");
    }

    public DefaultReplayEngine() {
        this(new DefaultReplayAuthority());
    }

    @Override
    public ReplayResult replay(ReplayManifest manifest, ReplayEnvironment environment) {
        Objects.requireNonNull(manifest, "manifest cannot be null");
        Objects.requireNonNull(environment, "environment cannot be null");

        String execId = manifest.sourceExecutionId();
        ReplayDecision decision = authority.authorize(execId, manifest);

        if (!decision.isPermitted()) {
            return ReplayResult.failure(execId, manifest.mode(), "Replay authorization denied: " + decision);
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("virtualTime", environment.clock().now().toString());
        summary.put("randomSample", environment.random().nextLong());
        summary.put("stateSequence", manifest.stateSequence());
        summary.put("environmentId", environment.id());
        summary.put("decision", decision.name());

        return ReplayResult.success(execId, manifest.mode(), summary);
    }
}
