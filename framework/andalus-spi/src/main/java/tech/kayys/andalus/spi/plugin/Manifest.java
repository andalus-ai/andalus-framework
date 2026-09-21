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


import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.net.*;
import java.util.jar.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.yaml.*;

import tech.kayys.andalus.core.Permission;
import tech.kayys.andalus.extension.Version;
import tech.kayys.andalus.spi.plugin.Dependency;
import tech.kayys.andalus.resource.Resource;

/**
 * Manifest - describes a plugin or extension.
 * 
 * This is a first-class Resource in Andalus.
 */
public interface Manifest extends Resource {
    
    ManifestId id();
    
    String name();
    
    Version version();
    
    /**
     * Andalus Plugin API compatibility version.
     */
    default Version apiVersion() {
        return Version.VERSION_1_0_0;
    }
    
    String description();
    
    String mainClass();
    
    List<Dependency> dependencies();
    
    List<ProvidedCapability> provides();
    
    List<RequiredCapability> requires();
    
    List<Permission> permissions();
    
    Map<String, Object> configuration();
    
    List<String> authors();
    
    String license();
    
    String repository();
    
    String documentation();
    
    tech.kayys.andalus.extension.Metadata metadata();
    
    ManifestStatus status();
    
    Path location();
    
    Manifest withStatus(ManifestStatus status);
    
    default boolean hasDependency(String id) {
        return dependencies().stream().anyMatch(d -> d.id().equals(id));
    }
    
    default Optional<Dependency> findDependency(String id) {
        return dependencies().stream().filter(d -> d.id().equals(id)).findFirst();
    }
    
    default boolean provides(String capability) {
        return provides().stream().anyMatch(p -> p.id().equals(capability));
    }
    
    default boolean requires(String capability) {
        return requires().stream().anyMatch(r -> r.id().equals(capability));
    }
}
