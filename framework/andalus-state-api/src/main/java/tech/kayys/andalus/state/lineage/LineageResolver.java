package tech.kayys.andalus.state.lineage;

import tech.kayys.andalus.state.artifact.ArtifactId;

public interface LineageResolver {
    LineageGraph resolve(ArtifactId artifactId);
    LineageGraph resolveExecution(String executionId);
}
