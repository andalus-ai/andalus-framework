package tech.kayys.wayang.harness.artifact;

import java.net.URI;

/**
 * Handle representing the status of an artifact export operation.
 */
public interface ExportHandle {

    ArtifactId artifactId();

    URI destination();

    boolean isComplete();
}
