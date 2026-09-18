package tech.kayys.wayang.harness.semantics;

import java.util.Objects;

/**
 * Standard implementation of {@link EffectAnalyzer}.
 */
public class DefaultEffectAnalyzer implements EffectAnalyzer {

    @Override
    public EffectDescriptor analyze(Operation operation) {
        Objects.requireNonNull(operation, "Operation cannot be null");
        return operation.effects();
    }

    @Override
    public boolean isSafeForAutoRetry(EffectDescriptor descriptor) {
        if (descriptor == null) {
            return false;
        }
        if (descriptor.kinds().contains(EffectKind.IRREVERSIBLE)) {
            return false;
        }
        if (descriptor.reversibility() == Reversibility.NON_REVERSIBLE) {
            return false;
        }
        return descriptor.idempotency() == Idempotency.PURE
                || descriptor.idempotency() == Idempotency.IDEMPOTENT
                || descriptor.idempotency() == Idempotency.KEYED;
    }

    @Override
    public boolean requiresIsolation(EffectDescriptor descriptor) {
        if (descriptor == null) {
            return true;
        }
        if (descriptor.scope() == EffectScope.LOCAL_SYSTEM
                || descriptor.scope() == EffectScope.EXTERNAL_SYSTEM
                || descriptor.scope() == EffectScope.GLOBAL) {
            return true;
        }
        if (descriptor.kinds().contains(EffectKind.PROCESS)
                || descriptor.kinds().contains(EffectKind.EXECUTE)
                || descriptor.kinds().contains(EffectKind.DELETE)) {
            return true;
        }
        return descriptor.resourceImpact().requiresFilesystem() || descriptor.resourceImpact().requiresNetwork();
    }
}
