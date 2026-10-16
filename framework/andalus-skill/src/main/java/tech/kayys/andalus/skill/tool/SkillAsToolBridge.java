package tech.kayys.andalus.skill.tool;

import tech.kayys.andalus.skill.runtime.SkillExecutionRequest;
import tech.kayys.andalus.skill.runtime.SkillRuntime;
import tech.kayys.andalus.skill.spi.SkillReference;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Bridges a Skill into the Tool calling system through {@link SkillRuntime}.
 *
 * <p><strong>Governance remains at the ToolRuntime layer above this bridge,
 * not inside this class.</strong> Policy, capability, and sandbox checks are
 * the responsibility of the ToolRuntime that wraps this adapter. This class
 * is intentionally thin — it only translates Tool invocation arguments and
 * execution lineage into a {@link SkillExecutionRequest} and delegates to
 * {@link SkillRuntime}.</p>
 *
 * <p>Design per Phase 2F spec:</p>
 * <pre>
 * LLM / Agent
 *   ↓
 * ToolRuntime   ← policy, capability, sandbox
 *   ↓
 * SkillAsToolBridge
 *   ↓
 * SkillRuntime  ← resolve, context, execute
 *   ↓
 * Skill
 * </pre>
 */
public final class SkillAsToolBridge {

    private final SkillReference skill;
    private final SkillRuntime skillRuntime;

    public SkillAsToolBridge(SkillReference skill, SkillRuntime skillRuntime) {
        this.skill = Objects.requireNonNull(skill, "skill");
        this.skillRuntime = Objects.requireNonNull(skillRuntime, "skillRuntime");
    }

    /**
     * Execute the skill through {@link SkillRuntime}, propagating execution lineage.
     *
     * <p>Tool call arguments map directly to Skill inputs:</p>
     * <pre>
     * ToolInvocation.arguments()  →  SkillExecutionRequest.inputs()
     *                             →  SkillContext.getInputs()
     * </pre>
     *
     * @param arguments   Tool call arguments to pass as Skill inputs
     * @param executionId enclosing agent execution identifier
     * @param traceId     distributed trace identifier
     * @param turnId      parent turn identifier
     * @return a future that completes with the Skill result
     */
    public CompletionStage<SkillResult> execute(
            Map<String, Object> arguments,
            String executionId,
            String traceId,
            String turnId) {

        SkillExecutionRequest request = new SkillExecutionRequest(
                skill,
                arguments,
                Optional.empty(),
                executionId != null ? executionId : "",
                traceId     != null ? traceId     : "",
                turnId      != null ? turnId      : "",
                Map.of());

        return skillRuntime.execute(request);
    }

    /** The Skill reference this bridge is bound to. */
    public SkillReference skill() {
        return skill;
    }
}
