package tech.kayys.wayang.spi.sandbox.artifact;

import java.io.InputStream;

public record ArtifactDownloadResult(
        ArtifactDescriptor descriptor,
        InputStream content
) {
}
