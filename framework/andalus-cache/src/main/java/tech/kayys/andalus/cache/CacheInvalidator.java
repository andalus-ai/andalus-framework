package tech.kayys.andalus.cache;

import tech.kayys.andalus.project.ProjectContext;

/**
 * Handles complex cache invalidation logic based on external triggers or project updates.
 */
public interface CacheInvalidator {
    
    /**
     * Invalidate all cache entries derived from a specific artifact.
     */
    void invalidateForArtifact(ProjectContext project, String artifactId);
}
