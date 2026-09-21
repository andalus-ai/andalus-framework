package tech.kayys.andalus.project;

/**
 * Represents the root isolation boundary for a project in Andalus.
 * Every persistent operation must carry this context to ensure isolation.
 */
public record ProjectContext(
    String projectId,
    String tenantId,
    String userId,
    String workspaceId
) {}
