package tech.kayys.wayang.harness.spi;

/**
 * Defines the contract for harness operations in the Wayang framework.
 */

public interface Harness {
    HarnessResult execute(HarnessRequest request);
}
