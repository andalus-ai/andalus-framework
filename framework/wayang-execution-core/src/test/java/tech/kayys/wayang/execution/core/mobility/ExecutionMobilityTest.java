package tech.kayys.wayang.execution.core.mobility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.wayang.execution.core.checkpoint.InMemoryExecutionCheckpointStore;
import tech.kayys.wayang.execution.mobility.ExecutionCursor;
import tech.kayys.wayang.execution.mobility.ExecutionDurableState;
import tech.kayys.wayang.execution.mobility.ExecutionIdentity;
import tech.kayys.wayang.execution.mobility.ExecutionLease;
import tech.kayys.wayang.execution.mobility.ExecutionLocation;
import tech.kayys.wayang.execution.mobility.ExecutionStatus;
import tech.kayys.wayang.execution.mobility.FencingToken;
import tech.kayys.wayang.execution.mobility.MigrationPreparation;
import tech.kayys.wayang.execution.mobility.MigrationReason;
import tech.kayys.wayang.execution.mobility.MigrationResult;
import tech.kayys.wayang.execution.mobility.state.ExecutionContextEnvelope;
import tech.kayys.wayang.execution.mobility.state.ReconstructionResult;
import tech.kayys.wayang.execution.mobility.state.StateDurability;
import tech.kayys.wayang.execution.mobility.state.StateEntry;
import tech.kayys.wayang.execution.mobility.state.StateManifest;
import tech.kayys.wayang.execution.mobility.state.StateReference;
import tech.kayys.wayang.execution.mobility.workspace.WorkspaceEntry;
import tech.kayys.wayang.execution.mobility.workspace.WorkspaceSnapshot;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionMobilityTest {

    private DefaultLeaseManager leaseManager;
    private InMemoryExecutionStateStore stateStore;
    private InMemoryExecutionCheckpointStore checkpointStore;
    private DefaultMigrationCoordinator migrationCoordinator;
    private DefaultExecutionMigrationManager migrationManager;
    private DefaultExecutionMobility executionMobility;
    private InMemoryWorkspaceStore workspaceStore;
    private DefaultStateReconstructor stateReconstructor;

    @BeforeEach
    void setUp() {
        leaseManager = new DefaultLeaseManager();
        stateStore = new InMemoryExecutionStateStore(leaseManager);
        checkpointStore = new InMemoryExecutionCheckpointStore();
        migrationCoordinator = new DefaultMigrationCoordinator(leaseManager, stateStore, checkpointStore);
        migrationManager = new DefaultExecutionMigrationManager(migrationCoordinator);
        executionMobility = new DefaultExecutionMobility(stateStore, checkpointStore, leaseManager, migrationManager);
        workspaceStore = new InMemoryWorkspaceStore();
        stateReconstructor = new DefaultStateReconstructor();
    }

    @Test
    @DisplayName("ExecutionIdentity and ExecutionLocation preserve identity across locations")
    void testIdentityAndLocation() {
        ExecutionIdentity identity = ExecutionIdentity.of("exec-42", "wf-17", "aljabr");
        assertEquals("exec-42", identity.id());
        assertEquals("wf-17", identity.workflowId());
        assertEquals("aljabr", identity.agentId());
        assertNotNull(identity.createdAt());

        ExecutionLocation loc1 = ExecutionLocation.of("runtime-local", "binding-1");
        assertEquals("runtime-local", loc1.runtimeId());
        assertEquals("binding-1", loc1.runtimeBinding());

        ExecutionLocation loc2 = ExecutionLocation.of("runtime-cloud", "binding-2");
        assertEquals("runtime-cloud", loc2.runtimeId());
        // Identity remains identical
        assertEquals("exec-42", identity.id());
    }

    @Test
    @DisplayName("LeaseManager generates monotonic fencing tokens and enforces leases")
    void testLeaseManagerFencingTokens() {
        String execId = "exec-lease-1";

        // Acquire first lease
        ExecutionLease lease1 = leaseManager.acquireLease(execId, "worker-A", Duration.ofMinutes(5));
        assertEquals(1L, lease1.fencingToken().generation());
        assertEquals("worker-A", lease1.holderId());
        assertFalse(lease1.isExpired());
        assertTrue(leaseManager.validateToken(execId, lease1.fencingToken()));

        // Renew lease
        boolean renewed = leaseManager.renewLease(execId, "worker-A", Duration.ofMinutes(10));
        assertTrue(renewed);

        // Acquire lease for new holder (simulating migration/handoff)
        ExecutionLease lease2 = leaseManager.acquireLease(execId, "worker-B", Duration.ofMinutes(5));
        assertEquals(2L, lease2.fencingToken().generation());
        assertEquals("worker-B", lease2.holderId());

        // Old token is superseded
        assertFalse(leaseManager.validateToken(execId, lease1.fencingToken()));
        assertTrue(leaseManager.validateToken(execId, lease2.fencingToken()));

        // Release lease
        leaseManager.releaseLease(execId, "worker-B");
        assertTrue(leaseManager.getLease(execId).isEmpty());
    }

    @Test
    @DisplayName("StateStore rejects writes with stale fencing tokens or lower sequences")
    void testStateStoreFencingAndConcurrency() {
        String execId = "exec-state-1";
        ExecutionIdentity identity = ExecutionIdentity.of(execId, "wf-1", "agent-1");
        ExecutionCursor cursor = ExecutionCursor.of("wf-1", "node-1", 1, "RUNNING");

        ExecutionLease lease = leaseManager.acquireLease(execId, "runtime-A", Duration.ofMinutes(5));
        FencingToken token1 = lease.fencingToken();

        // 1. Initial save succeeds
        ExecutionDurableState s1 = ExecutionDurableState.of(identity, cursor);
        stateStore.save(s1, token1);
        assertEquals(1L, stateStore.version(execId));

        // 2. Next sequence succeeds
        ExecutionDurableState s2 = s1.withStatus(ExecutionStatus.RUNNING);
        stateStore.save(s2, token1);
        assertEquals(2L, stateStore.version(execId));

        // 3. Lower sequence violates optimistic concurrency
        ExecutionDurableState sStaleSeq = new ExecutionDurableState(identity, ExecutionStatus.RUNNING, cursor, 1L, "sha256:0", null);
        assertThrows(IllegalStateException.class, () -> stateStore.save(sStaleSeq, token1));

        // 4. Token generation incremented by new lease
        ExecutionLease lease2 = leaseManager.acquireLease(execId, "runtime-B", Duration.ofMinutes(5));
        FencingToken token2 = lease2.fencingToken();
        assertTrue(token2.generation() > token1.generation());

        // Stale token write rejected
        ExecutionDurableState s3 = s2.withStatus(ExecutionStatus.RUNNING);
        assertThrows(IllegalStateException.class, () -> stateStore.save(s3, token1));

        // New token write succeeds
        stateStore.save(s3, token2);
        assertEquals(3L, stateStore.version(execId));

        // Transition history audit log
        var transitions = stateStore.getTransitions(execId);
        assertFalse(transitions.isEmpty());
        assertEquals(3, transitions.size());
    }

    @Test
    @DisplayName("Two-phase MigrationCoordinator successfully prepares and commits migration")
    void testTwoPhaseMigrationCoordinator() {
        String execId = "exec-mig-1";
        ExecutionIdentity identity = ExecutionIdentity.of(execId, "wf-cloud", "agent-x");
        ExecutionCursor cursor = ExecutionCursor.of("wf-cloud", "task-3", 1, "RUNNING");

        ExecutionLease leaseA = leaseManager.acquireLease(execId, "local-runtime", Duration.ofMinutes(10));
        ExecutionDurableState state = ExecutionDurableState.of(identity, cursor);
        stateStore.save(state, leaseA.fencingToken());

        // Phase 1: Prepare migration to cloud-runtime
        MigrationPreparation prep = migrationCoordinator.prepare(execId, "cloud-runtime", MigrationReason.RESOURCE_PRESSURE);
        assertNotNull(prep);
        assertEquals("local-runtime", prep.sourceRuntimeId());
        assertEquals("cloud-runtime", prep.targetRuntimeId());

        // State is now MIGRATING
        ExecutionDurableState migratingState = stateStore.load(execId).orElseThrow();
        assertEquals(ExecutionStatus.MIGRATING, migratingState.status());

        // Phase 2: Commit migration
        MigrationResult result = migrationCoordinator.commit(prep);
        assertTrue(result.success());
        assertEquals("local-runtime", result.sourceRuntimeId());
        assertEquals("cloud-runtime", result.targetRuntimeId());

        // State is now RUNNING under new lease
        ExecutionDurableState finalState = stateStore.load(execId).orElseThrow();
        assertEquals(ExecutionStatus.RUNNING, finalState.status());

        // Active lease belongs to target runtime with new fencing token
        ExecutionLease activeLease = leaseManager.getLease(execId).orElseThrow();
        assertEquals("cloud-runtime", activeLease.holderId());
        assertTrue(activeLease.fencingToken().generation() > leaseA.fencingToken().generation());

        // Provenance receipt recorded
        Optional<tech.kayys.wayang.execution.mobility.MigrationReceipt> receipt = migrationCoordinator.getReceipt(execId);
        assertTrue(receipt.isPresent());
        assertEquals(MigrationReason.RESOURCE_PRESSURE, receipt.get().reason());
    }

    @Test
    @DisplayName("MigrationCoordinator abort preserves source state")
    void testMigrationCoordinatorAbort() {
        String execId = "exec-abort-1";
        ExecutionIdentity identity = ExecutionIdentity.of(execId, "wf-abort", "agent-y");
        ExecutionCursor cursor = ExecutionCursor.of("wf-abort", "step-1", 1, "RUNNING");

        ExecutionLease leaseA = leaseManager.acquireLease(execId, "runtime-A", Duration.ofMinutes(10));
        ExecutionDurableState state = ExecutionDurableState.of(identity, cursor);
        stateStore.save(state, leaseA.fencingToken());

        MigrationPreparation prep = migrationCoordinator.prepare(execId, "runtime-B", MigrationReason.SCALE);
        assertEquals(ExecutionStatus.MIGRATING, stateStore.load(execId).orElseThrow().status());

        migrationCoordinator.abort(prep);
        assertEquals(ExecutionStatus.SUSPENDED, stateStore.load(execId).orElseThrow().status());
    }

    @Test
    @DisplayName("ExecutionMobility end-to-end checkpoint, suspend, restore, migrate")
    void testExecutionMobilityLifecycle() {
        String execId = "exec-mob-lifecycle";
        ExecutionIdentity identity = ExecutionIdentity.of(execId, "wf-life", "agent-z");
        ExecutionCursor cursor = ExecutionCursor.of("wf-life", "step-1", 1, "RUNNING");

        ExecutionLease lease = leaseManager.acquireLease(execId, "runtime-A", Duration.ofMinutes(10));
        ExecutionDurableState state = ExecutionDurableState.of(identity, cursor);
        stateStore.save(state, lease.fencingToken());

        // Checkpoint
        ExecutionCheckpoint cp = executionMobility.checkpoint(execId);
        assertNotNull(cp);
        assertEquals(execId, cp.executionId());

        // Suspend
        executionMobility.suspend(execId);
        assertEquals(ExecutionStatus.SUSPENDED, stateStore.load(execId).orElseThrow().status());
        assertTrue(leaseManager.getLease(execId).isEmpty());

        // Re-acquire lease and Restore
        ExecutionLease lease2 = leaseManager.acquireLease(execId, "runtime-A", Duration.ofMinutes(10));
        executionMobility.restore(execId, cp.checkpointId());
        assertEquals(ExecutionStatus.RUNNING, stateStore.load(execId).orElseThrow().status());

        // Migrate to runtime-B
        MigrationResult migResult = executionMobility.migrate(execId, "runtime-B", MigrationReason.LOCAL_TO_REMOTE);
        assertTrue(migResult.success());
        assertEquals("runtime-B", migResult.targetRuntimeId());
    }

    @Test
    @DisplayName("WorkspaceStore snapshots and restores workspace state")
    void testWorkspaceSnapshotAndRestore() {
        WorkspaceSnapshot snap = workspaceStore.snapshot("ws-100");
        assertNotNull(snap);
        assertEquals("ws-100", snap.workspaceId());
        assertFalse(snap.entries().isEmpty());

        workspaceStore.restore(snap, "ws-200");
        Optional<WorkspaceSnapshot> restored = workspaceStore.getLatest("ws-200");
        assertTrue(restored.isPresent());
        assertEquals(snap.digest(), restored.get().digest());
    }

    @Test
    @DisplayName("ExecutionContextEnvelope and StateReconstructor reconstruct runtime context")
    void testContextReconstruction() {
        StateReference stateRef = StateReference.of("state-1", "agent-context", "sha256:ctx1", 512L, "memory://state1");
        StateReference artRef = StateReference.of("art-1", "build-artifact", "sha256:art1", 2048L, "storage://art1");
        StateReference cpRef = StateReference.of("cp-1", "checkpoint", "sha256:cp1", 1024L, "s3://cp1");

        ExecutionContextEnvelope env = new ExecutionContextEnvelope(
                "exec-recon-1",
                5L,
                Map.of("agentContext", stateRef),
                List.of(artRef),
                List.of(cpRef)
        );

        ReconstructionResult res = stateReconstructor.reconstruct(env, "target-cloud-node");
        assertTrue(res.success());
        assertEquals("exec-recon-1", res.executionId());
        assertEquals("target-cloud-node", res.reconstructedMetadata().get("targetRuntimeId"));
        assertEquals(5L, res.reconstructedMetadata().get("versionSequence"));
    }

    @Test
    @DisplayName("StateManifest and StateEntry model durable vs ephemeral boundaries")
    void testStateManifestAndDurability() {
        StateEntry durableEntry = new StateEntry("workspace", "filesystem", "sha256:ws", 4096L, StateDurability.DURABLE);
        StateEntry cacheEntry = new StateEntry("build-cache", "cache", "sha256:cache", 8192L, StateDurability.CACHE);
        StateEntry ephemeralEntry = new StateEntry("temp-pipe", "ipc", "sha256:pipe", 128L, StateDurability.EPHEMERAL);

        StateManifest manifest = new StateManifest("exec-manifest-1", 1L, List.of(durableEntry, cacheEntry, ephemeralEntry), "sha256:manifest");
        assertEquals(3, manifest.entries().size());
        assertEquals(StateDurability.DURABLE, manifest.entries().get(0).durability());
        assertEquals(StateDurability.CACHE, manifest.entries().get(1).durability());
        assertEquals(StateDurability.EPHEMERAL, manifest.entries().get(2).durability());
    }
}
