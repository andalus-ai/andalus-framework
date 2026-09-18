package tech.kayys.wayang.harness.semantics;

import java.util.Set;

/**
 * Declares the aggregate semantic effects, blast scope, reversibility, and idempotency of an operation.
 */
public interface EffectDescriptor {

    Set<EffectKind> kinds();

    EffectScope scope();

    Reversibility reversibility();

    Idempotency idempotency();

    ResourceImpact resourceImpact();
}
