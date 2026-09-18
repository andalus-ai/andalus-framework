package tech.kayys.wayang.harness.workspace.v3;

import tech.kayys.wayang.harness.artifact.ArtifactId;

import java.util.Objects;

/**
 * Base reference against which a workspace diff is computed.
 */
public sealed interface DiffBase
        permits DiffBase.InitialState,
                DiffBase.SnapshotBase,
                DiffBase.ArtifactBase {

    record InitialState() implements DiffBase {}

    record SnapshotBase(SnapshotId snapshotId) implements DiffBase {
        public SnapshotBase {
            Objects.requireNonNull(snapshotId, "snapshotId");
        }
    }

    record ArtifactBase(ArtifactId artifactId) implements DiffBase {
        public ArtifactBase {
            Objects.requireNonNull(artifactId, "artifactId");
        }
    }
}
