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
import java.util.Map;
import java.util.Objects;

import tech.kayys.andalus.artifact.ArtifactFormat;
import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.identity.ResourceId.ArtifactId;

/**
 * Text Artifact
 */
public final record TextArtifact(
    ArtifactId id,
    ResourceType type,
    ArtifactFormat format,
    String data,
    Metadata metadata,
    ArtifactMetadata artifactMetadata
) implements Artifact {
    
    public TextArtifact {
        Objects.requireNonNull(data, "content cannot be null");
        if (metadata == null) {
            metadata = Metadata.builder()
                .name("text-artifact")
                .label("type", type.name())
                .label("format", format.name())
                .now()
                .build();
        }
        if (artifactMetadata == null) {
            artifactMetadata = new ArtifactMetadata(null, 0, null, null, null, null, null, null, Collections.emptyMap());
        }
    }
    
    public static TextArtifact of(String data) {
        return new TextArtifact(
            new ResourceId.ArtifactId(Id.random()),
            ArtifactType.TEXT,
            ArtifactFormat.TEXT,
            data,
            null,
            new ArtifactMetadata(null, 0, null, null, null, null, null, null, Map.of())
        );
    }
    
    public static TextArtifact of(String data, String name) {
        return new TextArtifact(
            new ResourceId.ArtifactId(Id.random()),
            ArtifactType.TEXT,
            ArtifactFormat.TEXT,
            data,
            Metadata.builder().name(name).now().build(),
            new ArtifactMetadata(null, 0, null, null, null, null, null, null, Map.of())
        );
    }
    
    public static TextArtifact of(String data, Metadata metadata) {
        return new TextArtifact(
            new ResourceId.ArtifactId(Id.random()),
            ArtifactType.TEXT,
            ArtifactFormat.TEXT,
            data,
            metadata,
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
    public Object content() { return data; }
    
    @Override
    public String asString() { return data; }
    
    @Override
    public byte[] asBytes() { 
        return data.getBytes(StandardCharsets.UTF_8);
    }
    
    @Override
    public tech.kayys.andalus.extension.Metadata metadata() { return metadata; }
    
    
}
