package tech.kayys.andalus.spi.sandbox.artifact;

import java.io.InputStream;

public record ArtifactDownloadResult(
        ArtifactDescriptor descriptor,
        InputStream content
) {
}
