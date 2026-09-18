package tech.kayys.wayang.knowledge.snapshot.pack;

import java.util.Map;

/**
 * Represents a knowledge snapshot package resource.
 *
 * <p>Its components capture `resource id`, `resource type`, `resource version`, `fingerprint`, `media type`, and other values.</p>
 *
 * @param resourceId the resource id
 * @param resourceType the resource type
 * @param resourceVersion the resource version
 * @param fingerprint the fingerprint
 * @param mediaType the media type
 * @param content the content
 * @param metadata the metadata
 */


public record KnowledgeSnapshotPackageResource(
        String resourceId,
        String resourceType,
        String resourceVersion,
        String fingerprint,
        String mediaType,
        byte[] content,
        Map<String, String> metadata
) {
    public KnowledgeSnapshotPackageResource {
        content = content == null ? new byte[0] : content.clone();
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    @Override
    public byte[] content() {
        return content.clone();
    }
}
