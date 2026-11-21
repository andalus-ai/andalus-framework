package tech.kayys.andalus.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ExecutionSnapshotTest {

    @Test
    void builderPreservesSuppliedIdentityAndCreationTime() {
        UUID snapshotId = UUID.randomUUID();
        UUID executionId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-02T03:04:05Z");

        ExecutionSnapshot snapshot = ExecutionSnapshot.builder()
                .id(snapshotId)
                .executionId(executionId)
                .createdAt(createdAt)
                .build();

        assertEquals(snapshotId, snapshot.getId());
        assertEquals(executionId, snapshot.getExecutionId());
        assertEquals(createdAt, snapshot.getCreatedAt());
    }

    @Test
    void builderGeneratesIdentityAndCreationTimeWhenOmitted() {
        ExecutionSnapshot snapshot = ExecutionSnapshot.builder()
                .executionId(UUID.randomUUID())
                .build();

        assertNotNull(snapshot.getId());
        assertNotNull(snapshot.getCreatedAt());
    }

    @Test
    void publicConstructorContinuesGeneratingIdentityAndCreationTime() {
        ExecutionSnapshot snapshot = new ExecutionSnapshot(UUID.randomUUID(), null);

        assertNotNull(snapshot.getId());
        assertNotNull(snapshot.getCreatedAt());
    }
}
