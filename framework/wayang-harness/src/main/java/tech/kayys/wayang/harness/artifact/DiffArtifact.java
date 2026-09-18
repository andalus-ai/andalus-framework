package tech.kayys.wayang.harness.artifact;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * First-class artifact representing unified or structured code diffs / patches.
 */
public interface DiffArtifact extends Artifact {

    DiffFormat format();

    Collection<FileChange> changes();

    enum DiffFormat {
        UNIFIED,
        STRUCTURED,
        JSON,
        CUSTOM
    }

    record FileChange(
            String path,
            ChangeType changeType,
            String diffText
    ) {
        public FileChange {
            Objects.requireNonNull(path, "path");
            changeType = changeType != null ? changeType : ChangeType.MODIFIED;
            diffText = diffText != null ? diffText : "";
        }
    }

    enum ChangeType {
        ADDED,
        MODIFIED,
        DELETED,
        RENAMED
    }
}
