package tech.kayys.wayang.harness.spi;

/**
 * Defines the contract for harness phase operations in the Wayang framework.
 */

public interface HarnessPhase<I, O> {
    O execute(I input, HarnessContext context, HarnessRuntime runtime);
}
