package tech.kayys.wayang.execution.governance;

/**
 * Declares the scope of an authorization policy.
 */
public enum AuthorizationScope {

    PLATFORM,

    TENANT,

    AGENT,

    USER,

    ROLE,

    RESOURCE,

    EXECUTION
}
