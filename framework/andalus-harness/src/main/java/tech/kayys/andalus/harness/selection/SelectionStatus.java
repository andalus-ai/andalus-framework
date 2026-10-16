package tech.kayys.andalus.harness.selection;

/**
 * Describes the overall outcome of a tool/skill selection operation.
 *
 * <p>These statuses describe the <em>selection</em> operation, not authorization
 * or execution success.</p>
 */
public enum SelectionStatus {
    /** One or more compatible candidates were found. */
    CANDIDATES_FOUND,
    /** Discovery completed but no compatible candidate exists. */
    NO_CANDIDATES,
    /** The context does not contain enough information to identify candidates. */
    INSUFFICIENT_INFORMATION,
    /** A specific capability was requested but is not registered. */
    CAPABILITY_UNAVAILABLE,
    /** Execution constraints or requirements conflict and cannot be satisfied. */
    CONSTRAINT_CONFLICT,
    /** The selection operation itself failed due to an unexpected error. */
    FAILED
}
