package tech.kayys.wayang.state.lineage;

import tech.kayys.wayang.state.artifact.ArtifactId;

public interface LineageResolver {
    LineageGraph resolve(ArtifactId artifactId);
    LineageGraph resolveExecution(String executionId);
}
