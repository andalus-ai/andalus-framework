package tech.kayys.wayang.harness.phase;

import tech.kayys.wayang.harness.context.HarnessContext;
import tech.kayys.wayang.harness.runtime.HarnessRuntime;

/**
 * Modular execution phase in the Harness processing pipeline.
 *
 * @param <I> phase input type
 * @param <O> phase output type
 */
public interface HarnessPhase<I, O> {

    O execute(I input, HarnessContext context, HarnessRuntime runtime);
}
