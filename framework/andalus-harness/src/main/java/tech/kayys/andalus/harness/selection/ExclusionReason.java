package tech.kayys.andalus.harness.selection;

/**
 * Reason why a candidate was excluded from the selection result.
 *
 * <p>Authorization denials remain owned by the security layer.
 * {@link ExclusionReason} covers only technical incompatibilities.</p>
 */
public enum ExclusionReason {
    /** The candidate does not provide the required capability. */
    CAPABILITY_MISMATCH,
    /** The candidate's input schema is incompatible with the request requirements. */
    INPUT_SCHEMA_MISMATCH,
    /** The candidate cannot produce an output compatible with what the task needs. */
    OUTPUT_INCOMPATIBLE,
    /** The candidate is explicitly disabled in the registry or configuration. */
    DISABLED,
    /** The requested version of the candidate is not available. */
    VERSION_UNAVAILABLE,
    /** A resource constraint (budget, quota, or concurrency limit) prevents selection. */
    RESOURCE_CONSTRAINT,
    /** The candidate type does not match the requested {@link SelectionTarget}. */
    UNSUPPORTED_TARGET
}
