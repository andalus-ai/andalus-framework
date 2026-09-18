package tech.kayys.wayang.knowledge.exchange;

import tech.kayys.wayang.knowledge.snapshot.artifact.KnowledgeEvidenceArtifactId;
import tech.kayys.wayang.knowledge.snapshot.merkle.KnowledgeEvidenceMerkleProof;
import tech.kayys.wayang.knowledge.snapshot.pack.KnowledgeSnapshotVerificationManifest;

import java.util.Map;

/**
 * Represents a knowledge evidence exchange response.
 *
 * <p>Its components capture `success`, `operation`, `artifact id`, `content`, `media type`, and other values.</p>
 *
 * @param success the success
 * @param operation the operation
 * @param artifactId the artifact id
 * @param content the content
 * @param mediaType the media type
 * @param manifest the manifest
 * @param merkleProof the merkle proof
 * @param errorCode the error code
 * @param errorMessage the error message
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeResponse(
        boolean success,
        KnowledgeEvidenceExchangeOperation operation,
        KnowledgeEvidenceArtifactId artifactId,
        byte[] content,
        String mediaType,
        KnowledgeSnapshotVerificationManifest manifest,
        KnowledgeEvidenceMerkleProof merkleProof,
        String errorCode,
        String errorMessage,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceExchangeResponse {
        content = content == null ? new byte[0] : content.clone();
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    @Override
    public byte[] content() {
        return content.clone();
    }
}
