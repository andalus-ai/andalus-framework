package tech.kayys.andalus.harness.selection;

/**
 * Identifies which category of candidate is eligible for a selection request.
 */
public enum SelectionTarget {
    /** Only tool candidates should be evaluated. */
    TOOL,
    /** Only skill candidates should be evaluated. */
    SKILL,
    /** Both tool and skill candidates are eligible. */
    TOOL_OR_SKILL
}
