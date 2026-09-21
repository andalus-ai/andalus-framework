package tech.kayys.andalus.migration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.List;
import java.util.Optional;

import tech.kayys.andalus.extension.Version;
import tech.kayys.andalus.resource.Resource;

/**
 * Migration Registry
 */
public interface MigrationRegistry {
    
    <T extends Resource> void register(Migration<T> migration);
    
    <T extends Resource> List<Migration<T>> getMigrations(Class<T> type);
    
    <T extends Resource> Optional<Migration<T>> getMigration(Version fromVersion, Version toVersion);
    
    <T extends Resource> T migrate(T resource, Version targetVersion) throws Exception;
}