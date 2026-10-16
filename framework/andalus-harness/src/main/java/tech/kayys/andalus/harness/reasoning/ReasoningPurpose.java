package tech.kayys.andalus.harness.reasoning;

/**
 * The purpose of a reasoning operation — what kind of assessment is being requested.
 *
 * <p>These purposes are not separate reasoning engines; they are inputs to the
 * same reasoning runtime that guide the provider towards producing an
 * assessment relevant to the caller's need.</p>
 */
public enum ReasoningPurpose {
    /** Interpret observations or evidence from tool results, memory, or prior turns. */
    INTERPRET_EVIDENCE,
    /** Compare candidate approaches, plans, or explanations against explicit criteria. */
    ASSESS_ALTERNATIVES,
    /** Identify missing information, unverified assumptions, or conflicting evidence. */
    IDENTIFY_UNCERTAINTY,
    /** Assess a proposed plan for missing prerequisites, conflicts, or unsupported assumptions. */
    EVALUATE_PLAN,
    /** Produce a concise, publishable explanation for a decision. */
    EXPLAIN_DECISION,
    /** Evaluate observed outcomes against stated completion criteria. */
    ASSESS_OUTCOME
}
