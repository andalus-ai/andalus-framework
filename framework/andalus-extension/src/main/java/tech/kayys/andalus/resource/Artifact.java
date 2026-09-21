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
import java.util.*;

import tech.kayys.andalus.artifact.ArtifactFormat;
import tech.kayys.andalus.identity.ResourceId.ArtifactId;

/**
 * Artifact - everything produced during execution.
 */
public interface Artifact extends Resource {
    
    ArtifactId id();
    
    ResourceType type();
    
    ArtifactFormat format();
    
    Object content();
    
    String asString();
    
    byte[] asBytes();
    
    tech.kayys.andalus.extension.Metadata metadata();
    
    default ArtifactMetadata artifactMetadata() {
        return null;
    }
    
    default List<ArtifactRepresentation> representations() {
        return List.of();
    }
    
    default boolean isText() {
        return type() == ArtifactType.TEXT;
    }
    
    default boolean isJson() {
        return format() == ArtifactFormat.JSON;
    }
    
    default boolean isStreaming() {
        return format() == ArtifactFormat.STREAM;
    }
}
