package tech.kayys.andalus.execution.core.portability;

import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.mobility.state.StateReference;
import tech.kayys.andalus.execution.portability.ArtifactManifest;
import tech.kayys.andalus.execution.portability.ExecutionExporter;
import tech.kayys.andalus.execution.portability.ExecutionManifest;
import tech.kayys.andalus.execution.portability.ExecutionPackage;
import tech.kayys.andalus.execution.portability.PortableExecution;
import tech.kayys.andalus.execution.portability.ProvenanceReference;
import tech.kayys.andalus.execution.portability.Replayability;
import tech.kayys.andalus.execution.provenance.ProvenanceChain;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

/**
 * Exporter producing self-contained, verified portable execution packages.
 */
public class DefaultExecutionExporter implements ExecutionExporter {

    @Override
    public PortableExecution export(ExecutionIdentity executionId, ExecutionDurableState state, ProvenanceChain provenance) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(state, "state cannot be null");

        String merkleRoot = provenance != null && provenance.chainDigest() != null ? provenance.chainDigest() : "none";
        long eventCount = provenance != null && provenance.nodes() != null ? provenance.nodes().size() : 0L;
        ProvenanceReference provRef = ProvenanceReference.of(
            provenance != null && provenance.executionId() != null ? provenance.executionId() : "prov-" + executionId.id(),
            merkleRoot,
            eventCount
        );

        StateReference stateRef = StateReference.of(
            "state-" + executionId.id(),
            "DURABLE_STATE",
            state.versionDigest(),
            1024L,
            "in-memory"
        );

        ExecutionManifest manifest = new ExecutionManifest(
            executionId,
            Set.of(),
            Set.of(),
            Set.of(),
            "SANDBOXED",
            stateRef,
            provRef,
            Replayability.ofDeterministic(),
            ArtifactManifest.empty()
        );

        byte[] payload = ("{\"executionId\":\"" + executionId.id() + "\",\"sequence\":" + state.versionSequence() + "}").getBytes(StandardCharsets.UTF_8);
        return ExecutionPackage.of(manifest, payload).execution();
    }
}
