package tech.kayys.andalus.skill.runtime;

/**
 * Describes why a skill resolution attempt succeeded or failed.
 *
 * <p>Keeping status distinct from the presence/absence of the skill itself
 * lets observability tooling distinguish actionable error conditions without
 * inspecting exception messages.</p>
 */
public enum ResolutionStatus {

    /** A skill implementation was found and returned. */
    RESOLVED,

    /** No provider has a skill with the requested identifier. */
    NOT_FOUND,

    /** A skill exists but the requested version is not available. */
    VERSION_UNAVAILABLE,

    /**
     * A provider was found but cannot satisfy the capability requirements
     * expressed in the resolution request.
     */
    INCOMPATIBLE,

    /**
     * The Skill was found and is enabled, but one or more of its declared
     * required capabilities are not available in the current runtime.
     * This is distinct from {@link #INCOMPATIBLE} — the Skill itself is not
     * the problem; the environment is missing the required infrastructure.
     */
    CAPABILITY_UNAVAILABLE,

    /**
     * A skill is installed but has been administratively disabled; it will not
     * be served until re-enabled.
     */
    DISABLED,

    /**
     * A resolution policy prevented this request from being forwarded to
     * providers (e.g., remote resolution is disabled in community mode).
     */
    DENIED,

    /** A provider encountered an unexpected infrastructure failure. */
    ERROR
}
