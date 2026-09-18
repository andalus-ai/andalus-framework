package tech.kayys.wayang.spi.sandbox.artifact;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ArtifactTransferService {

    CompletionStage<ArtifactDescriptor> upload(ArtifactUploadRequest request);

    CompletionStage<ArtifactDownloadResult> download(ArtifactDownloadRequest request);

    CompletionStage<Void> delete(String sandboxId, String artifactId);

    Optional<ArtifactDescriptor> find(String sandboxId, String artifactId);
}
