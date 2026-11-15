# Execution Recovery and Lifecycle Consumer Map

- **Status:** Source inventory in progress; build/runtime verification pending.
- **Audit date:** 2026-11-15
- **Repositories:** `andalus-ai/andalus-framework` and `andalus-ai/andalus`
- **Related decisions:** [ADR-0002 — Checkpoint contract ownership](ADR-0002-checkpoint-contract-ownership.md), [ADR-0003 — Execution lifecycle, cancellation and resume](ADR-0003-execution-lifecycle-cancellation-resume.md), [M0 foundation audit](M0-FOUNDATION-AUDIT.md)
- **Purpose:** Map the current checkpoint, lifecycle and cancellation types to their source-level consumers before choosing a migration path. This document is an evidence map, not an implementation approval.

## 1. Executive findings

The current repositories contain several distinct models. They must not be collapsed merely because they use similar names.

1. **The live Runtime resume entry point reads the Framework Runtime-SPI `CheckpointStore` containing `AgentContext`.** It does not restore the context from Runtime's newer `ExecutionCheckpointStore`.
2. **During live ReAct execution, Runtime writes two checkpoint paths.** The lifecycle path writes a Runtime-local `ExecutionCheckpoint`; the ReAct bridge then writes the semantic `AgentContext` to the Framework Runtime-SPI store. These writes are sequential and are not one transaction.
3. **The newer Runtime checkpoint store is consulted for status hydration**, but the Runtime checkpoint's `AgentContext` is not the source used by `AgentExecutionService.resume`.
4. **Both current Runtime checkpoint implementations are in-memory.** The legacy store also keeps cache-entry references in a separate map. The inspected code does not provide process-restart durability or an atomic context-plus-cache-reference record.
5. **Framework already contains multiple lifecycle/cancellation systems:** invocation-level `ExecutionContext` + `CancellationToken`; an execution API lifecycle state machine; and a harness durable lifecycle coordinator/cancellation controller. These have different packages and scopes. This map has not established that they are all wired into Runtime's ReAct execution path.
6. **Runtime's current ReAct path does not visibly receive a Framework cancellation token.** Its `pause()` and `cancel()` methods update status, but the inspected code does not signal the active ReAct loop, model stream or tool executor.
7. **Framework's portable execution checkpoint API is a promising persistence port, not yet a drop-in replacement.** Runtime's `ExecutionCheckpoint` stores a live `AgentContext` object and runtime-specific status/phase fields; Framework's record stores immutable payload bytes, attempt identity, sequence and integrity manifest. An explicit versioned codec/adapter is required.

No source code was changed by this audit. No Maven build, tests, dependency resolution, runtime restart test or serialization round-trip test was run.

## 2. Repository revisions inspected

| Repository | Default branch revision inspected | Evidence |
|---|---|---|
| `andalus-ai/andalus-framework` | `8e2a2a430b4038fd7266ed46853961682750ef2f` | Recursive Git tree and targeted source fetches |
| `andalus-ai/andalus` | `ef7237d85a7825fb4121f8ed0ac341cfc810ac1b` | Recursive Git tree and targeted source fetches |

The revisions above identify the source snapshots inspected for this map. They are not claims that builds or tests pass.

## 3. Runtime checkpoint contracts and live call graph

### 3.1 Contract inventory

| Contract/model | Source | Payload / semantics | Current role |
|---|---|---|---|
| Framework Runtime-SPI `CheckpointStore` | Framework: `framework/andalus-runtime-spi/src/main/java/tech/kayys/andalus/execution/CheckpointStore.java` | `String executionId` ↔ `AgentContext`; save/load/history/delete | Legacy semantic context snapshot port; injected by Runtime |
| Runtime legacy implementation | Runtime: `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/InMemoryCheckpointStore.java` | In-memory ordered AgentContext references plus a separate cache-reference map | Implements the Framework Runtime-SPI port |
| Runtime `ExecutionCheckpoint` | Runtime: `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/ExecutionCheckpoint.java` | Checkpoint ID, execution ID, Runtime status/phase, attempt, iteration, direct `AgentContext` reference, timestamp and metadata | Runtime-specific lifecycle checkpoint record |
| Runtime `ExecutionCheckpointStore` | Runtime: `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/ExecutionCheckpointStore.java` | Save/load latest or by ID/history/delete Runtime checkpoint records | Newer Runtime lifecycle checkpoint port |
| Runtime in-memory implementation | Runtime: `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/InMemoryExecutionCheckpointStore.java` | Process-local lists of Runtime checkpoint objects | Default fallback when no CDI store is injected |
| Framework execution checkpoint API | Framework: `framework/andalus-execution-api/src/main/java/tech/kayys/andalus/execution/checkpoint/CheckpointStore.java` | Atomic append, lookup by ID, latest by execution, ordered history | Preferred portable execution checkpoint persistence port in ADR-0002 |
| Framework execution checkpoint record | Framework: `framework/andalus-execution-api/src/main/java/tech/kayys/andalus/execution/checkpoint/ExecutionCheckpoint.java` | Immutable payload bytes, attempt ID, sequence, manifest and integrity metadata | Versionable transport/storage envelope |
| Framework execution checkpoint in-memory adapter | Framework: `framework/andalus-execution-core/src/main/java/tech/kayys/andalus/execution/core/checkpoint/InMemoryExecutionCheckpointStore.java` | In-memory implementation of the Framework execution checkpoint API | Framework recovery/mobility tests and local use |
| Framework state checkpoint API | Framework: `framework/andalus-state-api/src/main/java/tech/kayys/andalus/state/checkpoint/CheckpointStore.java` | State-layer `Checkpoint` records | Separate state/provenance abstraction; not an alias for execution checkpoint records |
| Framework engine snapshot port | Framework: `framework/andalus-engine/src/main/java/tech/kayys/andalus/execution/CheckpointStore.java` | Engine `ExecutionSnapshot` values | Same-FQN collision with Runtime-SPI store; no implementation/call site confirmed in targeted search |

The two contracts with FQN `tech.kayys.andalus.execution.CheckpointStore` are incompatible and belong to different Maven artifacts. Do not add a compatibility alias under that same FQN.

### 3.2 Live Runtime write path

The inspected source establishes this sequence:

```text
AgentExecutionService.create(...)
  ├─ CDI injects Framework runtime-SPI CheckpointStore
  ├─ optionally resolves Runtime ExecutionCheckpointStore
  └─ constructs DefaultAgentExecution(...)
       ├─ uses injected Runtime store, or creates InMemoryExecutionCheckpointStore
       └─ constructs DefaultExecutionStateStore(legacyStore, eventLedger, runtimeStore)

DefaultAgentExecution.runAgentLoop(...)
  └─ ReActAgent CheckpointBridge(execId, AgentContext)
       ├─ DefaultExecutionStateStore.checkpoint(...)
       │    ├─ creates Runtime ExecutionCheckpoint
       │    ├─ saves it to Runtime ExecutionCheckpointStore
       │    ├─ updates in-memory AgentExecutionState checkpoint ID
       │    └─ appends CHECKPOINT_CREATED to EventLedger when configured
       └─ Framework runtime-SPI CheckpointStore.save(execId, AgentContext)
            └─ InMemoryCheckpointStore stores AgentContext reference
                 └─ optionally records cache IDs separately
```

Source locations:

- `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/AgentExecutionService.java`
- `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/DefaultAgentExecution.java`
- `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/DefaultExecutionStateStore.java`
- `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/InMemoryCheckpointStore.java`
- `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/InMemoryExecutionCheckpointStore.java`

The ReAct bridge is configured before the agent is built. It is invoked at model-step checkpoint boundaries by the ReAct integration. The inspected `onDone` and stub-completion paths also explicitly write to both stores. The exact number and timing of ReAct bridge invocations should be confirmed with a focused runtime test.

**Consistency issue:** the lifecycle checkpoint write occurs before the legacy AgentContext save. If the second write fails, the stores disagree. If the first write fails, the legacy store is not reached. The EventLedger append occurs after the Runtime checkpoint save and in-memory state update, so it is another separately failing operation.

### 3.3 Live Runtime resume path

The current `AgentExecutionService.resume(executionId)` does this:

1. Calls the Framework Runtime-SPI `checkpointStore.load(executionId)`.
2. Fails with `IllegalArgumentException` if no AgentContext is found.
3. Constructs a new `DefaultAgentExecution` using that loaded AgentContext.
4. Resolves the Runtime `ExecutionCheckpointStore` separately.
5. Calls `execution.resume()` and returns the execution.

The `DefaultAgentExecution` constructor loads the latest Runtime `ExecutionCheckpoint` if present and copies only its status into the in-memory state store. It does not use that record's `context()` as the resumed AgentContext. Thus:

- Context restoration is anchored on the legacy Framework Runtime-SPI store.
- Lifecycle status hydration is anchored on the Runtime-local store when a record exists.
- The two paths can disagree without a reconciliation check.
- The inspected `resume()` method changes PAUSED to RUNNING; it does not itself restart the ReAct loop. The service's `resume()` returns the reconstructed execution. The approval path separately calls `execute()` for a specific decision case.

This is a source-level call-graph finding, not a claim about all possible external callers.

### 3.4 Storage and cache-reference behavior

- `InMemoryCheckpointStore` stores `AgentContext` object references in a `CopyOnWriteArrayList`; it does not serialize a stable payload.
- Cache IDs are held in a separate `cacheRefs` map keyed only by execution ID. A new non-empty list replaces the previous value; an empty list does not clear an older value.
- The context history and cache reference snapshot are not one atomic record or versioned together.
- `InMemoryExecutionCheckpointStore` stores Runtime checkpoint objects in process-local `CopyOnWriteArrayList` values. It has no persistence/restart guarantee and does not enforce sequence, unique IDs or idempotency.
- Both implementations are annotated as CDI application-scoped in the inspected source, but application scope does not make them durable across process restart.

The current Runtime code therefore supports in-process snapshots, not crash-safe recovery.

## 4. Runtime lifecycle and cancellation consumers

| Type | Source | Observed behavior / consumers |
|---|---|---|
| `AgentExecutionService` | Runtime: `.../execution/AgentExecutionService.java` | Creates and reconstructs `DefaultAgentExecution`; resume reads legacy AgentContext store |
| `DefaultAgentExecution` | Runtime: `.../execution/DefaultAgentExecution.java` | Drives ReAct asynchronously; owns public pause/resume/cancel methods; callback completion/failure updates status |
| `ExecutionStateStore` | Runtime: `.../execution/ExecutionStateStore.java` | Runtime state API for phase, status, checkpoint, completion and failure |
| `DefaultExecutionStateStore` | Runtime: `.../execution/DefaultExecutionStateStore.java` | In-memory state map; transitionStatus validates expected status and transition table; changeStatus and fail have separate unchecked mutation paths |
| `ExecutionLifecycleRules` | Runtime: `.../execution/ExecutionLifecycleRules.java` | Runtime-specific transition table; notably permits FAILED/ERROR/TIMEOUT → RUNNING, so those states are retryable rather than terminal in this model |
| `AgentExecutionState` | Runtime: `.../execution/AgentExecutionState.java` | In-memory projection of status, phase, attempt, iteration, checkpoint ID, timestamps, deadline and idempotency metadata |

### Observed lifecycle gaps

- `DefaultExecutionStateStore.fail` writes FAILED directly rather than routing through `ExecutionLifecycleRules.requireTransition`.
- `changeStatus` writes an arbitrary status; its constructor/hydration use should be separated from live transition authority.
- `DefaultAgentExecution.pause` checkpoints and transitions RUNNING → PAUSED but does not signal or suspend the active ReAct/model/tool operation.
- `resume` changes PAUSED → RUNNING but does not resume the suspended loop.
- `cancel` changes status but does not visibly propagate a cancellation token to ReAct, model streaming or tool execution.
- The asynchronous listener's `onDone` path sets the local status to COMPLETED and then calls the state store's validated completion path. If cancellation won first, this can attempt an invalid terminal transition from CANCELLED.
- `onError`, outer exception handling and completion callbacks are separate paths; terminal outcome arbitration is not centralized in the inspected class.
- The execution budget contains a maximum duration, but the inspected ReAct bridge does not connect it to a Framework cancellation/deadline token.

These observations identify implementation paths to test; they do not establish behavior for every provider implementation.

## 5. Framework lifecycle and cancellation inventory

Framework already contains several concepts with overlapping vocabulary. They have distinct package scopes and should be evaluated before adding new contracts.

| Contract/implementation | Source | Scope and observed role |
|---|---|---|
| Invocation `ExecutionContext` | `framework/andalus-execution-api/.../execution/ExecutionContext.java` | Invocation metadata, cancellation token, security snapshot and invocation lifecycle |
| `CancellationToken` | `framework/andalus-execution-api/.../execution/CancellationToken.java` | Thread-safe cancellation signal contract with idempotent cancel and callback registration |
| `DefaultCancellationToken` | `framework/andalus-execution-core/.../execution/core/DefaultCancellationToken.java` | Default token implementation |
| `ExecutionLifecycle` + transitions | `framework/andalus-execution-api/.../execution/ExecutionLifecycle.java`; `framework/andalus-execution-core/.../execution/core/ExecutionLifecycleTransitions.java` | Invocation lifecycle: CREATED/AUTHORIZING/AUTHORIZED/WAITING_OBLIGATION/RUNNING/SUSPENDED/RESUMING/COMPLETED/FAILED/CANCELLED |
| `ExecutionStateMachine` | `framework/andalus-execution-api/.../execution/lifecycle/ExecutionStateMachine.java` | Separate state-machine abstraction; requires consumer mapping |
| `ExecutionLifecycleState` | `framework/andalus-execution-api/.../execution/control/ExecutionLifecycleState.java` | Richer control-plane lifecycle states including PAUSING, CHECKPOINTING, MIGRATING and RECOVERING |
| `CancellationScope` | `framework/andalus-execution-api/.../execution/control/CancellationScope.java` | Cancellation scope and child-cascade metadata |
| Harness `ExecutionLifecycleCoordinator` | `framework/andalus-harness/.../execution/lifecycle/DefaultExecutionLifecycleCoordinator.java` | Persists harness durable state before updating an in-process lifecycle projection |
| Harness `DurableCancellationController` | `framework/andalus-harness/.../scheduling/cancellation/DurableCancellationController.java` | Delegates cancellation to harness `DurableExecutionControl` |
| Tool `CancellationToken` | `framework/andalus-tool/.../tool/CancellationToken.java` | Tool-scoped cancellation abstraction; relationship to execution token needs adapter/consumer mapping |
| Engine `ReasoningExecutionContext` | `framework/andalus-engine/.../reasoner/ReasoningExecutionContext.java` | Reasoning-scoped cancellation/deadline checks; distinct from Runtime ReAct execution |

### Important boundary

The invocation-level execution API, harness durable execution coordinator, Runtime `ExecutionStatus` state, tool cancellation API and reasoning context are not one state machine. The source tree shows their existence, but this audit has not established an end-to-end adapter path from Runtime's `DefaultAgentExecution` to all Framework cancellation facilities.

Do not add another cancellation token or lifecycle enum until the current contracts' public consumers and adapters are mapped. Prefer explicit adapters and conformance tests where scopes genuinely differ.

## 6. Framework checkpoint consumers

| API | Implementation / consumers identified | Notes |
|---|---|---|
| `andalus-execution-api` checkpoint `CheckpointStore` | `andalus-execution-core/.../InMemoryExecutionCheckpointStore.java`; `DefaultRecoveryManager`, `DefaultExecutionMobility`, `DefaultMigrationCoordinator` | Has sequence, payload, manifest/integrity and atomic-append requirements; in-memory implementation is not durable |
| `andalus-state-api` checkpoint `CheckpointStore` | `andalus-state-core/.../InMemoryCheckpointStore.java` | State-layer checkpoint contract; keep separate unless consumer semantics prove overlap |
| Engine `CheckpointStore` | `andalus-engine/.../execution/CheckpointStore.java` | Targeted search found no implementation/call site beyond declaration; has unsafe null default for latest lookup |
| Runtime-SPI `CheckpointStore` | Implemented in Runtime by `InMemoryCheckpointStore` | Runtime's legacy AgentContext recovery path; same FQN as engine contract |

Framework execution recovery tests identified in the tree include:

- `framework/andalus-execution-core/src/test/java/tech/kayys/andalus/execution/core/ExecutionCheckpointTest.java`
- `framework/andalus-execution-core/src/test/java/tech/kayys/andalus/execution/core/ExecutionRecoveryTest.java`
- `framework/andalus-execution-core/src/test/java/tech/kayys/andalus/execution/core/CheckpointRestorationTest.java`
- `framework/andalus-execution-core/src/test/java/tech/kayys/andalus/execution/core/RecoveryReconciliationTest.java`
- `framework/andalus-execution-core/src/test/java/tech/kayys/andalus/execution/core/CancellationTokenTest.java`
- `framework/andalus-execution-core/src/test/java/tech/kayys/andalus/execution/core/ExecutionLifecycleTransitionsTest.java`
- `framework/andalus-execution-core/src/test/java/tech/kayys/andalus/execution/core/ExecutionStateMachineTest.java`

The existence of these tests does not prove that they pass or that Runtime's ReAct execution path uses the tested components.

## 7. Serialization, persistence and compatibility gaps

The following items remain unverified and block a safe migration:

1. **AgentContext codec:** no versioned serialization codec or serializer registration for Runtime's direct `AgentContext` checkpoint was identified in the targeted paths. Confirm whether other modules provide one.
2. **Payload schema:** define which request, message history, tool results, pending approvals, model/provider identity, tool idempotency keys, cache references and other state must survive resume.
3. **Framework envelope mapping:** map Runtime status/phase/attempt/iteration and AgentContext into a versioned payload plus Framework manifest/attempt/sequence metadata.
4. **Attempt identity:** Runtime's current checkpoint record has integer attempt but no Framework `AttemptId`; define stable mapping.
5. **Sequence:** Runtime's checkpoint record has no sequence field; do not infer ordering solely from wall-clock timestamps or list insertion once a durable multi-writer adapter exists.
6. **Cache recovery:** define whether cache entries are required for correctness, optional performance hints, or derived artifacts. Persist references with the checkpoint version if needed.
7. **Event ledger:** define whether events are authoritative, replayable, or diagnostic projections. The current checkpoint/event writes are not atomic.
8. **External consumers:** search downstream modules, extension examples, published artifacts and third-party consumers before renaming/removing Runtime-SPI contracts.
9. **Failure model:** specify whether durable means process crash, host crash, storage-node failure or multi-region loss; the adapter's claim must match its guarantees.

## 8. Proposed ownership and migration direction

This map supports the direction proposed by ADR-0002 and ADR-0003, subject to completing the unresolved inventory:

- **Framework execution API:** own the portable execution checkpoint envelope and its conformance requirements.
- **Framework state API:** remain a separate state/provenance checkpoint capability.
- **Framework Runtime-SPI legacy AgentContext store:** migrate out of the colliding package and retain only as a temporary compatibility adapter if external consumers require it.
- **Runtime:** own live execution lifecycle arbitration, AgentContext codec/versioning, concrete durable store adapters, provider/tool cancellation wiring and resume orchestration.
- **Runtime checkpoint model:** replace or adapt the Runtime-local record only after its fields are mapped to the Framework envelope and no recovery information is lost.
- **Harness lifecycle/cancellation:** determine whether it is a separate durable harness/control-plane path or should coordinate with Runtime execution. Do not silently merge it with the ReAct lifecycle.
- **Engine snapshot store:** remove the duplicate FQN only after confirming no supported consumer depends on its graph snapshot semantics; if required, use a distinctly named engine-specific adapter.

### Migration sequence

1. Complete unresolved writer/reader/serializer/consumer inventory.
2. Add Runtime characterization tests for checkpoint order, pause, cancel, resume and callback races.
3. Define and test a versioned AgentContext codec.
4. Implement a Runtime adapter to the Framework execution checkpoint API; initially preserve the legacy read path.
5. Establish one authoritative checkpoint writer and a reconciliation/compatibility strategy.
6. Make resume load and validate the authoritative checkpoint and continue from a documented safe point.
7. Propagate cancellation/deadline through ReAct, provider streaming and tool execution.
8. Remove status-only pause behavior; report unsupported pause where safe suspension is not available.
9. Deprecate and remove legacy/duplicate contracts only after external consumer and binary compatibility checks.

## 9. Required next verification work

- [ ] Search all Framework and Runtime source for imports, implementations, injection points and method references to each checkpoint contract.
- [ ] Search for `AgentContext` serializers, Jackson modules, custom codecs, persistence adapters and schema registrations.
- [ ] Map ReAct checkpoint callback invocation frequency and the exact AgentContext instance passed at each checkpoint.
- [ ] Trace execution cache reads/writes and whether `loadCacheRefs` is consumed by resume in current source.
- [ ] Trace EventLedger implementations, persistence guarantees and resume/replay consumers.
- [ ] Map all model provider streaming APIs and tool executor paths to determine available interruption/cancellation hooks.
- [ ] Map Runtime's use (or non-use) of Framework `ExecutionContext`, `CancellationToken`, harness `DurableExecutionControl` and tool `CancellationToken`.
- [ ] Add characterization tests for two-store partial failure and latest-checkpoint disagreement.
- [ ] Add a process-restart test using a durable adapter before claiming crash-safe resume.
- [ ] Run clean Framework and Runtime builds/tests against pinned revisions and record exact results.

## 10. Exit criteria

The consumer map is implementation-ready only when all checkpoint and lifecycle contracts have:

- A complete declaration → implementation → writer → reader → serializer → test → external consumer map.
- A documented payload/schema and durability contract.
- An approved owner and migration/deprecation plan.
- Characterization and conformance tests that run successfully in both repositories.

Until then, this map is a targeted source-level inventory. It intentionally does not authorize deleting or renaming public APIs.
