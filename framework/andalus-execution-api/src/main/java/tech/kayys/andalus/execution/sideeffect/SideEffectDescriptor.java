package tech.kayys.andalus.execution.sideeffect;

import java.util.Optional;

public interface SideEffectDescriptor {
    SideEffectClass classification();
    Optional<IdempotencyKey> idempotencyKey();
    RecoveryBehavior recoveryBehavior();
}
