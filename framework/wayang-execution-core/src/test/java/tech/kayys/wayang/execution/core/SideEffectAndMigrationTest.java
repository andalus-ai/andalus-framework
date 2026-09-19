package tech.kayys.wayang.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.core.migration.DefaultMigrationPolicy;
import tech.kayys.wayang.execution.migration.MigrationDecision;
import tech.kayys.wayang.execution.migration.MigrationPlan;
import tech.kayys.wayang.execution.sideeffect.IdempotencyKey;
import tech.kayys.wayang.execution.sideeffect.RecoveryBehavior;
import tech.kayys.wayang.execution.sideeffect.SideEffectClass;
import tech.kayys.wayang.execution.sideeffect.SideEffectDescriptor;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SideEffectAndMigrationTest {

    @Test
    void testSideEffectDescriptor() {
        SideEffectDescriptor desc = new SideEffectDescriptor() {
            @Override
            public SideEffectClass classification() {
                return SideEffectClass.IDEMPOTENT;
            }

            @Override
            public Optional<IdempotencyKey> idempotencyKey() {
                return Optional.of(IdempotencyKey.of("txn-unique-42"));
            }

            @Override
            public RecoveryBehavior recoveryBehavior() {
                return RecoveryBehavior.SKIP_IF_COMMITTED;
            }
        };

        assertEquals(SideEffectClass.IDEMPOTENT, desc.classification());
        assertEquals("txn-unique-42", desc.idempotencyKey().orElseThrow().value());
        assertEquals(RecoveryBehavior.SKIP_IF_COMMITTED, desc.recoveryBehavior());
    }

    @Test
    void testMigrationPolicy() {
        DefaultMigrationPolicy policy = new DefaultMigrationPolicy();

        // Valid migration
        MigrationPlan valid = new MigrationPlan("exec-1", "worker-A", "worker-B", "cp-1", Instant.now(), Map.of());
        assertEquals(MigrationDecision.ALLOWED, policy.evaluate(valid));

        // Same worker migration denied
        MigrationPlan sameWorker = new MigrationPlan("exec-1", "worker-A", "worker-A", "cp-1", Instant.now(), Map.of());
        assertEquals(MigrationDecision.DENIED, policy.evaluate(sameWorker));

        // Missing checkpoint denied
        MigrationPlan noCp = new MigrationPlan("exec-1", "worker-A", "worker-B", null, Instant.now(), Map.of());
        assertEquals(MigrationDecision.DENIED, policy.evaluate(noCp));
    }
}
