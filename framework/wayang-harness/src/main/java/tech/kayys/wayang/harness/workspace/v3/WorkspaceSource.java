package tech.kayys.wayang.harness.workspace.v3;

import tech.kayys.wayang.harness.artifact.ArtifactId;

import java.net.URI;
import java.util.Objects;

/**
 * Origin source for initializing a workspace.
 */
public sealed interface WorkspaceSource
        permits WorkspaceSource.EmptyWorkspace,
                WorkspaceSource.RepositoryWorkspace,
                WorkspaceSource.SnapshotWorkspace,
                WorkspaceSource.ArtifactWorkspace,
                WorkspaceSource.CloneWorkspace {

    record EmptyWorkspace() implements WorkspaceSource {}

    record RepositoryWorkspace(URI repositoryUri, String revision) implements WorkspaceSource {
        public RepositoryWorkspace {
            Objects.requireNonNull(repositoryUri, "repositoryUri");
            revision = revision != null ? revision : "HEAD";
        }
    }

    record SnapshotWorkspace(SnapshotId snapshotId) implements WorkspaceSource {
        public SnapshotWorkspace {
            Objects.requireNonNull(snapshotId, "snapshotId");
        }
    }

    record ArtifactWorkspace(ArtifactId artifactId) implements WorkspaceSource {
        public ArtifactWorkspace {
            Objects.requireNonNull(artifactId, "artifactId");
        }
    }

    record CloneWorkspace(tech.kayys.wayang.harness.workspace.WorkspaceId sourceWorkspaceId) implements WorkspaceSource {
        public CloneWorkspace {
            Objects.requireNonNull(sourceWorkspaceId, "sourceWorkspaceId");
        }
    }
}
