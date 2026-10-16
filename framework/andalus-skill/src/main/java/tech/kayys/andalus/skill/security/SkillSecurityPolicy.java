package tech.kayys.andalus.skill.security;

import tech.kayys.andalus.skill.runtime.SkillExecutionRequest;

/**
 * Evaluates whether an invocation of a Skill is permitted.
 */
@FunctionalInterface
public interface SkillSecurityPolicy {

    /**
     * Evaluate the execution request against the current security context.
     *
     * @param request the execution request
     * @param context the caller security context
     * @return decision indicating whether invocation may proceed
     */
    SkillSecurityDecision evaluate(SkillExecutionRequest request, SkillSecurityContext context);

    /**
     * Permissive policy that allows all skill invocations (default for local/dev).
     */
    static SkillSecurityPolicy permissive() {
        return (request, context) -> SkillSecurityDecision.allow();
    }
}
