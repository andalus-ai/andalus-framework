package tech.kayys.wayang.harness.semantics;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SemanticsTest {

    @Test
    void testEffectDescriptorBuilder() {
        EffectDescriptor descriptor = DefaultEffectDescriptor.builder()
                .kind(EffectKind.WRITE)
                .kind(EffectKind.PROCESS)
                .scope(EffectScope.WORKSPACE)
                .reversibility(Reversibility.REVERSIBLE)
                .idempotency(Idempotency.IDEMPOTENT)
                .resourceImpact(ResourceImpact.standard(false, true))
                .build();

        assertEquals(2, descriptor.kinds().size());
        assertTrue(descriptor.kinds().contains(EffectKind.WRITE));
        assertTrue(descriptor.kinds().contains(EffectKind.PROCESS));
        assertEquals(EffectScope.WORKSPACE, descriptor.scope());
        assertEquals(Reversibility.REVERSIBLE, descriptor.reversibility());
        assertEquals(Idempotency.IDEMPOTENT, descriptor.idempotency());
        assertTrue(descriptor.resourceImpact().requiresFilesystem());
        assertFalse(descriptor.resourceImpact().requiresNetwork());
    }

    @Test
    void testEffectAnalyzerRetrySafety() {
        EffectAnalyzer analyzer = new DefaultEffectAnalyzer();

        // Pure operation is safe for retry
        EffectDescriptor pure = DefaultEffectDescriptor.builder()
                .kind(EffectKind.PURE)
                .reversibility(Reversibility.PURE)
                .idempotency(Idempotency.PURE)
                .build();
        assertTrue(analyzer.isSafeForAutoRetry(pure));

        // Idempotent write is safe for retry
        EffectDescriptor idempotent = DefaultEffectDescriptor.builder()
                .kind(EffectKind.WRITE)
                .reversibility(Reversibility.REVERSIBLE)
                .idempotency(Idempotency.IDEMPOTENT)
                .build();
        assertTrue(analyzer.isSafeForAutoRetry(idempotent));

        // Irreversible operation is NOT safe for retry
        EffectDescriptor irreversible = DefaultEffectDescriptor.builder()
                .kind(EffectKind.IRREVERSIBLE)
                .reversibility(Reversibility.NON_REVERSIBLE)
                .idempotency(Idempotency.NON_IDEMPOTENT)
                .build();
        assertFalse(analyzer.isSafeForAutoRetry(irreversible));
    }

    @Test
    void testEffectAnalyzerIsolationRequirement() {
        EffectAnalyzer analyzer = new DefaultEffectAnalyzer();

        // Local execution without filesystem/process is not strictly isolated
        EffectDescriptor pureLocal = DefaultEffectDescriptor.builder()
                .kind(EffectKind.PURE)
                .scope(EffectScope.EXECUTION)
                .build();
        assertFalse(analyzer.requiresIsolation(pureLocal));

        // Process execution requires isolation
        EffectDescriptor processOp = DefaultEffectDescriptor.builder()
                .kind(EffectKind.EXECUTE)
                .kind(EffectKind.PROCESS)
                .scope(EffectScope.LOCAL_SYSTEM)
                .build();
        assertTrue(analyzer.requiresIsolation(processOp));
    }

    @Test
    void testOperationConstruction() {
        EffectDescriptor effects = DefaultEffectDescriptor.builder()
                .kind(EffectKind.READ)
                .scope(EffectScope.WORKSPACE)
                .build();

        Operation op = DefaultOperation.of(
                OperationType.READ,
                OperationIntent.of("Read pom.xml", Map.of("path", "pom.xml")),
                effects
        );

        assertEquals(OperationType.READ, op.type());
        assertEquals("Read pom.xml", op.intent().summary());
        assertEquals("pom.xml", op.intent().parameters().get("path"));
        assertNotNull(op.id());
    }
}
