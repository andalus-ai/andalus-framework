package tech.kayys.wayang.execution.sideeffect;

import java.util.Optional;

public interface SideEffectDescriptor {
    SideEffectClass classification();
    Optional<IdempotencyKey> idempotencyKey();
    RecoveryBehavior recoveryBehavior();
}
