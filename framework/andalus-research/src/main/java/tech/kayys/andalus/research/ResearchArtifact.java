package tech.kayys.andalus.research;

import tech.kayys.andalus.project.ProjectContext;

/**
 * An artifact produced by a research step (e.g. a downloaded source, extracted fact, or summary).
 */
public record ResearchArtifact(
    String id,
    ProjectContext project,
    String sessionId,
    String stepId,
    String contentType,
    String content
) {}
