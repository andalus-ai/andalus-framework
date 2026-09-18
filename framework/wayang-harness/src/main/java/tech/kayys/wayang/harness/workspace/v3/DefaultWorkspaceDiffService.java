package tech.kayys.wayang.harness.workspace.v3;

import tech.kayys.wayang.harness.artifact.ArtifactDigest;
import tech.kayys.wayang.harness.artifact.ArtifactId;
import tech.kayys.wayang.harness.artifact.ArtifactMetadata;
import tech.kayys.wayang.harness.artifact.ArtifactProvenance;
import tech.kayys.wayang.harness.artifact.ArtifactType;
import tech.kayys.wayang.harness.artifact.ArtifactDescriptor;
import tech.kayys.wayang.harness.artifact.ArtifactRetention;
import tech.kayys.wayang.harness.artifact.DefaultDiffArtifact;
import tech.kayys.wayang.harness.artifact.DiffArtifact;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.workspace.WorkspaceId;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default implementation of {@link WorkspaceDiffService}.
 */
public class DefaultWorkspaceDiffService implements WorkspaceDiffService {

    @Override
    public DiffArtifact diff(ExecutionId executionId, WorkspaceId workspaceId, DiffBase base) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(workspaceId, "workspaceId");
        Objects.requireNonNull(base, "base");

        ArtifactId id = ArtifactId.generate();
        byte[] dummyDiff = "--- a/file.txt\n+++ b/file.txt\n@@ -1 +1 @@\n-old\n+new\n".getBytes(StandardCharsets.UTF_8);
        ArtifactDigest digest = ArtifactDigest.sha256(dummyDiff);

        ArtifactDescriptor descriptor = new ArtifactDescriptor(
                id,
                "diff-" + workspaceId.value() + ".patch",
                ArtifactType.PATCH,
                digest,
                ArtifactRetention.TEMPORARY,
                Map.of("workspace", workspaceId.value())
        );

        ArtifactMetadata metadata = new ArtifactMetadata(
                descriptor.name(),
                "text/x-diff",
                dummyDiff.length,
                java.time.Instant.now(),
                Map.of()
        );

        ArtifactProvenance provenance = ArtifactProvenance.ofExecution(executionId);

        List<DiffArtifact.FileChange> changes = List.of(
                new DiffArtifact.FileChange("file.txt", DiffArtifact.ChangeType.MODIFIED, new String(dummyDiff, StandardCharsets.UTF_8))
        );

        return new DefaultDiffArtifact(
                id,
                ArtifactType.PATCH,
                descriptor,
                digest,
                metadata,
                provenance,
                DiffArtifact.DiffFormat.UNIFIED,
                changes
        );
    }
}
