package tech.kayys.andalus.resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;

import tech.kayys.andalus.artifact.ArtifactFormat;
import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.identity.ResourceId.ArtifactId;

/**
 * JSON Artifact
 */
public final record JsonArtifact(
    ArtifactId id,
    ResourceType type,
    ArtifactFormat format,
    String json,
    Object parsed,
    Metadata metadata,
    ArtifactMetadata artifactMetadata
) implements Artifact {
    
    public JsonArtifact {
        Objects.requireNonNull(json, "json cannot be null");
        if (metadata == null) {
            metadata = Metadata.builder()
                .name("json-artifact")
                .label("type", type.name())
                .label("format", format.name())
                .now()
                .build();
        }
        if (artifactMetadata == null) {
            artifactMetadata = new ArtifactMetadata(null, 0, null, null, null, null, null, null, Collections.emptyMap());
        }
    }
    
    public static JsonArtifact of(String json) {
        return new JsonArtifact(
            new ResourceId.ArtifactId(Id.random()),
            ArtifactType.JSON,
            ArtifactFormat.JSON,
            json,
            null,
            null,
            new ArtifactMetadata(null, 0, null, null, null, null, null, null, Map.of())
        );
    }
    
    public static JsonArtifact of(String json, Object parsed) {
        return new JsonArtifact(
            new ResourceId.ArtifactId(Id.random()),
            ArtifactType.JSON,
            ArtifactFormat.JSON,
            json,
            parsed,
            null,
            new ArtifactMetadata(null, 0, null, null, null, null, null, null, Map.of())
        );
    }
    
    @Override
    public ArtifactId id() { return id; }
    
    @Override
    public ResourceType type() { return type; }
    
    @Override
    public ArtifactFormat format() { return format; }
    
    @Override
    public Object content() { return parsed != null ? parsed : json; }
    
    @Override
    public String asString() { return json; }
    
    @Override
    public byte[] asBytes() {
        return json.getBytes(StandardCharsets.UTF_8);
    }
    
    @Override
    public tech.kayys.andalus.extension.Metadata metadata() { return metadata; }
    
    
}