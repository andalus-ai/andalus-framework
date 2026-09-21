package tech.kayys.andalus.research;

import java.time.Instant;
import tech.kayys.andalus.project.ProjectContext;

/**
 * Represents a long-running, resumable research session.
 */
public record ResearchSession(
    String id,
    ProjectContext project,
    String question,
    ResearchStatus status,
    Instant startedAt,
    Instant completedAt
) {}
