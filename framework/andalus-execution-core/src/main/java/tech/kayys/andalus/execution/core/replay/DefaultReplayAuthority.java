package tech.kayys.andalus.execution.core.replay;

import tech.kayys.andalus.execution.replay.ReplayAuthority;
import tech.kayys.andalus.execution.replay.ReplayDecision;
import tech.kayys.andalus.execution.replay.ReplayManifest;
import tech.kayys.andalus.execution.replay.ReplayMode;

import java.util.Objects;

/**
 * Standard implementation of {@link ReplayAuthority}.
 */
public class DefaultReplayAuthority implements ReplayAuthority {

    @Override
    public ReplayDecision authorize(String executionId, ReplayManifest manifest) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(manifest, "manifest cannot be null");

        if (manifest.mode() == ReplayMode.REEXECUTE) {
            return ReplayDecision.REQUIRE_APPROVAL;
        }

        if (manifest.mode() == ReplayMode.SIMULATE) {
            return ReplayDecision.ALLOW_SIMULATION_ONLY;
        }

        if (manifest.mode() == ReplayMode.DRY_RUN) {
            return ReplayDecision.ALLOW_DRY_RUN;
        }

        return ReplayDecision.ALLOW;
    }
}
