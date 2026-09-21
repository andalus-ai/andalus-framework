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

import tech.kayys.andalus.extension.Version;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.ResourceType;

/**
 * Resource Version Manager
 */
public interface ResourceVersionManager {
    
    Version getCurrentVersion(Resource resource);
    
    boolean isLatestVersion(Resource resource);
    
    Resource upgrade(Resource resource) throws Exception;
    
    Resource downgrade(Resource resource, Version target) throws Exception;
    
    List<Version> getAvailableVersions(ResourceType type);
}