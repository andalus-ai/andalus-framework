package tech.kayys.wayang.spi.sandbox.artifact;

import java.io.InputStream;
import java.util.Optional;

public interface ArtifactStore {

    void put(ArtifactDescriptor descriptor, InputStream content) throws Exception;

    ArtifactDownloadResult get(String artifactId) throws Exception;

    Optional<ArtifactDescriptor> metadata(String artifactId);

    void delete(String artifactId) throws Exception;
}
