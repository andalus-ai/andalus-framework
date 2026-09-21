package tech.kayys.andalus.execution.secrets;

/**
 * Injection / consumption mechanism for a secret inside a sandbox.
 */
public enum SecretAccessMode {
    ENV,
    FILE,
    FD,
    IPC,
    TOKEN_PROVIDER,
    EPHEMERAL
}
