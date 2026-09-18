package tech.kayys.wayang.harness.environment.v3.secret;

/**
 * Provider SPI for acquiring governed access to secrets without exposing them broadly.
 */
public interface SecretProvider {

    SecretLease acquire(SecretRef ref, SecretAccessRequest request);
}
