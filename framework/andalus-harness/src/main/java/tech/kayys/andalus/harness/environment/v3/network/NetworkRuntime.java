package tech.kayys.andalus.harness.environment.v3.network;

/**
 * Runtime SPI for establishing network connections within security boundaries.
 */
public interface NetworkRuntime {

    NetworkConnection connect(NetworkRequest request);
}
