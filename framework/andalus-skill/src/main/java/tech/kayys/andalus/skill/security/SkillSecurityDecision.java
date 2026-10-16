package tech.kayys.andalus.skill.security;

import java.util.Map;
import java.util.Objects;

/**
 * Outcome of evaluating a security policy for a Skill invocation.
 */
public record SkillSecurityDecision(
        boolean allowed,
        String reason,
        Map<String, Object> evidence
) {

    public SkillSecurityDecision {
        reason   = reason   == null ? ""     : reason;
        evidence = evidence == null ? Map.of() : Map.copyOf(evidence);
    }

    public static SkillSecurityDecision allow() {
        return new SkillSecurityDecision(true, "Policy allows invocation", Map.of());
    }

    public static SkillSecurityDecision deny(String reason) {
        return new SkillSecurityDecision(false, reason, Map.of());
    }

    public static SkillSecurityDecision deny(String reason, Map<String, Object> evidence) {
        return new SkillSecurityDecision(false, reason, evidence);
    }
}
