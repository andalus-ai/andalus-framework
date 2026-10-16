package tech.kayys.andalus.harness.reflection;

/**
 * Classification of an analytical finding produced during reflection.
 */
public enum ReflectionFindingType {
    EXPECTED_OUTCOME_NOT_ACHIEVED,
    PLAN_DEVIATION,
    TOOL_FAILURE_PATTERN,
    INCOMPLETE_EVIDENCE,
    CONTRADICTING_EVIDENCE,
    ASSUMPTION_INVALIDATED,
    CONSTRAINT_MISSED,
    UNRESOLVED_ISSUE,
    POTENTIAL_IMPROVEMENT,
    OUTCOME_CONFIRMED
}
