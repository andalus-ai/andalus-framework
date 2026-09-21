package tech.kayys.andalus.spi.plugin;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import tech.kayys.andalus.extension.Id;
import tech.kayys.andalus.identity.ResourceId;
import tech.kayys.andalus.resource.ResourceType;

/**
 * Manifest ID - strongly typed
 */
public final record ManifestId(Id value) implements ResourceId {
    public static ManifestId random() {
        return new ManifestId(Id.random());
    }
    
    public static ManifestId fromString(String value) {
        return new ManifestId(Id.fromString(value));
    }
    
    @Override
    public ResourceType type() {
        return new ResourceType.Manifest();
    }
    
    @Override
    public String asString() {
        return value.asString();
    }
}
