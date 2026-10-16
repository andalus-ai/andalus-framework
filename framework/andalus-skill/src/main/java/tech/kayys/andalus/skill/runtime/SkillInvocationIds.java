package tech.kayys.andalus.skill.runtime;

import java.util.UUID;

/**
 * Utility for generating unique skill invocation identifiers.
 */
public final class SkillInvocationIds {

    private SkillInvocationIds() {
    }

    /**
     * Generates a new invocation ID.
     *
     * @return unique invocation identifier
     */
    public static String generate() {
        return "skill-" + UUID.randomUUID();
    }
}
