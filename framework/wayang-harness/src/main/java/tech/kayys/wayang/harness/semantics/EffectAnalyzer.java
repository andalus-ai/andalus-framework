package tech.kayys.wayang.harness.semantics;

/**
 * Evaluates semantic operations and inspects effect descriptors for safety, retryability, and isolation requirements.
 */
public interface EffectAnalyzer {

    EffectDescriptor analyze(Operation operation);

    boolean isSafeForAutoRetry(EffectDescriptor descriptor);

    boolean requiresIsolation(EffectDescriptor descriptor);
}
