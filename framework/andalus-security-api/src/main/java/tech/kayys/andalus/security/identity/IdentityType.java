package tech.kayys.andalus.security.identity;

/**
 * Classifies the type of principal in the Andalus security model.
 * Deliberately free of Keycloak, JWT, or protocol specifics.
 */
public enum IdentityType {
    USER,
    SERVICE,
    AGENT,
    SYSTEM,
    ANONYMOUS
}
