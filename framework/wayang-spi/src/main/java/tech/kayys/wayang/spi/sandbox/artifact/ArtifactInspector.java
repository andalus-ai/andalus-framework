package tech.kayys.wayang.spi.sandbox.artifact;

import java.io.InputStream;

public interface ArtifactInspector {
    ArtifactInspectionResult inspect(ArtifactDescriptor artifact, InputStream content) throws Exception;
}
