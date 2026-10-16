package tech.kayys.andalus.harness.protocol;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Execution-level operational constraints for an agent decision step.
 */
public record DecisionConstraints(
        Optional<Integer> maxActions,
        Optional<Duration> maxDecisionTime,
        Optional<Integer> maxDelegationDepth,
        boolean allowDelegation,
        boolean allowToolUse,
        boolean allowSkillUse,
        boolean allowKnowledgeRetrieval,
        boolean allowMemoryRetrieval,
        Map<String, Object> attributes
) {

    public DecisionConstraints {
        maxActions = maxActions == null ? Optional.empty() : maxActions;
        maxDecisionTime = maxDecisionTime == null ? Optional.empty() : maxDecisionTime;
        maxDelegationDepth = maxDelegationDepth == null ? Optional.empty() : maxDelegationDepth;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static DecisionConstraints defaults() {
        return new DecisionConstraints(
                Optional.empty(),
                Optional.empty(),
                Optional.of(3),
                true,
                true,
                true,
                true,
                true,
                Map.of()
        );
    }
}
