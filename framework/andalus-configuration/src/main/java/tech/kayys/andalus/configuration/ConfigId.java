package tech.kayys.andalus.configuration;
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
 * Configuration ID - strongly typed
 */
public final record ConfigId(Id value) implements ResourceId {
    public static ConfigId random() {
        return new ConfigId(Id.random());
    }
    
    public static ConfigId fromString(String value) {
        return new ConfigId(Id.fromString(value));
    }
    
    @Override
    public ResourceType type() {
        return new ResourceType.Configuration();
    }
    
    @Override
    public String asString() {
        return value.asString();
    }
}
