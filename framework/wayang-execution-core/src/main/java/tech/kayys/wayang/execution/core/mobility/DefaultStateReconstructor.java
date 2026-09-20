package tech.kayys.wayang.execution.core.mobility;

import tech.kayys.wayang.execution.mobility.state.ExecutionContextEnvelope;
import tech.kayys.wayang.execution.mobility.state.ReconstructionResult;
import tech.kayys.wayang.execution.mobility.state.StateReconstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Default implementation of {@link StateReconstructor} reconstructing runtime handles.
 */
public class DefaultStateReconstructor implements StateReconstructor {

    @Override
    public ReconstructionResult reconstruct(ExecutionContextEnvelope context, String targetRuntimeId) {
        Objects.requireNonNull(context, "context cannot be null");
        Objects.requireNonNull(targetRuntimeId, "targetRuntimeId cannot be null");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("targetRuntimeId", targetRuntimeId);
        metadata.put("executionId", context.executionId());
        metadata.put("versionSequence", context.versionSequence());
        metadata.put("stateEntriesReconstructed", context.state().size());
        metadata.put("artifactsReconstructed", context.artifacts().size());
        metadata.put("checkpointsReconstructed", context.checkpoints().size());

        return ReconstructionResult.success(context.executionId(), metadata);
    }
}
