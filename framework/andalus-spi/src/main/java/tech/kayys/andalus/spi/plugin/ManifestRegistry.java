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


import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import tech.kayys.andalus.spi.plugin.Manifest;
import tech.kayys.andalus.spi.plugin.ManifestId;
import tech.kayys.andalus.spi.plugin.ManifestStatus;

/**
 * Manifest Registry - manages manifests
 */
public interface ManifestRegistry {
    
    void register(Manifest manifest);
    
    void unregister(ManifestId id);
    
    Optional<Manifest> get(ManifestId id);
    
    Optional<Manifest> getByName(String name);
    
    List<Manifest> getAll();
    
    List<Manifest> getByStatus(ManifestStatus status);
    
    List<Manifest> getByCapability(String capability);
    
    boolean exists(ManifestId id);
    
    boolean existsByName(String name);
    
    void scanDirectory(Path directory) throws Exception;
}
