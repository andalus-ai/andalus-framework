package tech.kayys.andalus.harness.spi;

/**
 * Defines the contract for harness operations in the Andalus framework.
 */

public interface Harness {
    HarnessResult execute(HarnessRequest request);
}
