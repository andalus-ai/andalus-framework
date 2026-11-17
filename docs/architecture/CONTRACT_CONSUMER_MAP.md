# Contract Consumer and Implementation Map — Framework

Status: first-pass source map from the `docs/development-guidelines-and-agents` branch. This is not yet a complete repository-wide reference graph.

## 1. ExecutionContext contracts

| Contract | Definition | Confirmed consumer/implementation | Assessment |
|---|---|---|---|
| Invocation context | `framework/andalus-execution-api/.../execution/ExecutionContext.java` | `framework/andalus-execution-core/.../DefaultExecutionContext.java` implements it | Used for metadata, cancellation, security snapshot and invocation lifecycle |
| Graph/node context | `framework/andalus-engine/.../execution/ExecutionContext.java` | `framework/andalus-engine/.../ExecutionEngine.java` uses it in agent/workflow/skill method signatures | Separate graph execution abstraction despite identical FQN |
| Plugin execution context | `framework/andalus-plugin/.../plugin/execution/ExecutionContext.java` | No consumers inspected | Separate package and role; not a duplicate FQN |
| Tool execution context | `framework/andalus-tool/.../ToolExecutionContext.java` | `DefaultToolExecutionContext.java` | Explicitly tool-scoped; keep distinct |
| Model execution context | `framework/andalus-harness/.../ModelExecutionContext.java` | `DefaultModelExecutionContext.java` | Explicitly model-scoped; keep distinct |

### Important implementation mismatch

The execution API documentation says invalid lifecycle transitions should be rejected. However, `DefaultExecutionContext.transition` currently performs `lifecycle.set(next)` without validating the transition. This is a direct contract/implementation mismatch. The transition matrix should be centralized and tested before adding new lifecycle states.

## 2. Checkpoint contracts

| Contract | Definition | Confirmed implementation | Notes |
|---|---|---|---|
| Engine snapshot store | `framework/andalus-engine/.../engine/snapshot/EngineSnapshotStore.java` | No implementation confirmed in the inspected tree paths | Saves/restores engine `ExecutionSnapshot`; distinct FQN; `getLatest` returns `Optional` derived from `list()` by default |
| Execution checkpoint store | `framework/andalus-execution-api/.../execution/checkpoint/CheckpointStore.java` | `framework/andalus-execution-core/.../checkpoint/InMemoryExecutionCheckpointStore.java` | Stores execution checkpoint with attempt, sequence, payload and manifest |
| State checkpoint store | `framework/andalus-state-api/.../state/checkpoint/CheckpointStore.java` | `framework/andalus-state-core/.../InMemoryCheckpointStore.java` | Stores state/context/artifact/provenance envelope |
| AgentContext checkpoint store | `framework/andalus-runtime-spi/.../execution/CheckpointStore.java` | No framework-repository implementation confirmed in inspected paths; runtime repo has an implementation | Legacy/runtime-specific contract for AgentContext snapshots |

### Semantics that must remain explicit

The execution checkpoint API and state checkpoint API are not automatically interchangeable:
- Execution checkpoint has attempt identity, sequence, serialized payload and manifest.
- State checkpoint has state snapshot, context snapshot, artifact references and provenance offset.
- Engine snapshot uses `ExecutionSnapshot` with `ExecutionState`.
- Runtime-SPI checkpoint stores `AgentContext` directly.

The ADR should decide whether one is the canonical durable execution envelope and the others become adapters/projections, or whether separate stores are justified by distinct recovery boundaries.

## 3. Build/dependency observations

- Runtime-core directly declares dependencies on `andalus-execution-api`, `andalus-runtime-spi` and `andalus-engine`.
- `andalus-engine` declares its own same-package graph `ExecutionContext` and `ExecutionEngine` contract.
- `andalus-execution-core` depends on `andalus-execution-api` and provides the invocation-context implementation.
- The duplicate `ExecutionContext` FQN was a cross-artifact concern, not two declarations in one source module. The separate engine/runtime `CheckpointStore` FQN collision was removed by renaming the engine-local port to `EngineSnapshotStore` under `tech.kayys.andalus.engine.snapshot`.

A resolved Maven dependency tree and clean build are required to establish the actual compiler/classloader behavior.

## 4. Runtime integration evidence (2026-10-30)

The runtime repository was inspected at the same branch name. The following integration points are confirmed:

| Runtime path | Confirmed use | Migration implication |
|---|---|---|
| `runtime/andalus-runtime-core/.../DefaultExecutionEngine.java` | Implements the engine `ExecutionEngine`; agent, workflow and skill methods accept graph `ExecutionContext`; reads execution ID and variables | Migrate with the engine API signature; preserve variable extraction and execution identity |
| `runtime/andalus-runtime-core/.../SimpleExecutionContext.java` | Implements graph context and is created from `AgentRequest`; supplies variables/resources and child context; returns `null` for graph and current node | Replace with an explicit linear/request context or define safe graph absence semantics |
| `runtime/andalus-runtime-core/.../DefaultAndalusRuntime` | Creates `SimpleExecutionContext.fromRequest(request)` and passes it to `ExecutionEngine` (confirmed in prior source inspection) | Migrate request-to-execution boundary and define invocation-context attachment |
| `runtime/andalus-runtime-core/pom.xml` | Directly depends on both `andalus-execution-api` and `andalus-engine` | Confirms both artifacts are in the same runtime module's compile dependency set |
| `runtime/andalus-runtime-core/.../DefaultExecutionEngineTest.java` | Constructs `SimpleExecutionContext` and passes it to workflow/skill delegation methods | Update fixtures and add tests for explicit linear-context behavior |

The runtime's `DefaultExecutionEngine` uses graph context mainly as a carrier for execution ID and variable values on the agent path. Workflow and skill paths also extract variables. The request adapter deliberately returns `null` for graph and current-node access because the linear agent path does not use them. This is a concrete reason not to force every invocation into a graph-shaped context.

The runtime-core POM directly declares both framework artifacts. This confirms dependency-level exposure to the duplicate FQN, but does not by itself prove a specific compiler or class-loading failure. A clean dependency resolution and build remain necessary.

## 5. Consumer map status

Confirmed:
- `ExecutionEngine` uses the graph context.
- `DefaultExecutionContext` implements the invocation context.
- `InMemoryExecutionCheckpointStore` implements the execution checkpoint API.
- `InMemoryCheckpointStore` implements the state checkpoint API.
- Runtime repository's `AgentExecutionService` injects the runtime-SPI AgentContext checkpoint store.

Not yet confirmed:
- Every external module importing each contract.
- CDI registrations, service loaders and third-party integrations.
- All serialization/deserialization paths and persisted checkpoint compatibility.
- All tests and fixtures using legacy contracts.

## 6. ExecutionContext ownership decision record

A proposed decision record captures the separation of invocation and graph execution scopes: [ADR-0001 — Separate Invocation and Graph Execution Contexts](ADR-0001-execution-context-ownership.md). A second proposed record defines ownership boundaries for engine snapshots, execution checkpoint envelopes, state checkpoints and runtime AgentContext snapshots: [ADR-0002 — Establish Checkpoint Contract Ownership](ADR-0002-checkpoint-contract-ownership.md). Neither ADR authorizes an immediate source migration; the required reference maps, payload analysis and compatibility checks remain prerequisites.

## 7. Recommended decisions before source migration

1. Name the graph context distinctly, e.g. `GraphExecutionContext`, and reserve `ExecutionContext` for invocation-level context only if that ownership decision is approved.
2. Continue resolving checkpoint ownership and recovery semantics using ADR-0002; do not conflate engine snapshots, execution checkpoint envelopes, state checkpoints and AgentContext snapshots. The engine-local port has been given a distinct FQN; its runtime implementation and migration compatibility remain unverified.
3. Deprecate old contracts only after adapters and compatibility tests exist.
4. Enforce lifecycle transitions in `DefaultExecutionContext`.
5. Make checkpoint sequence writes atomic per execution and define duplicate/idempotency behavior.
6. Add cross-module contract tests and build checks that detect duplicate public FQNs across Maven artifacts.

## 8. Scope and limitations

This is a targeted source map, not a complete static-analysis report. The repository search connector did not provide a reliable complete code-reference index for these queries. All "not yet confirmed" items must be resolved by local grep/IDE indexing or a full source checkout before destructive API changes.


## 9. Runtime checkpoint and lifecycle source audit (2026-11-15)

This section records a targeted, line-by-line inspection of the current Runtime main-branch sources available through the GitHub file API. It supersedes earlier assumptions only where the exact source below was inspected. It is not a complete repository-wide reference graph: a local checkout, global symbol search, resolved dependency tree and test execution were unavailable in this environment.

### Confirmed runtime source paths

| Runtime source | Confirmed behavior | Consequence |
|---|---|---|
| `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/AgentExecutionService.java` | Injects the legacy `CheckpointStore`; separately discovers optional `ExecutionCheckpointStore`. `resume(executionId)` loads `Optional<AgentContext>` from the legacy store and fails if absent. | The runtime's resume payload remains anchored to the legacy AgentContext store, not the newer execution checkpoint record. |
| `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/DefaultAgentExecution.java` | Uses the supplied `ExecutionCheckpointStore`, but substitutes `InMemoryExecutionCheckpointStore` when none is injected. Constructor loads a checkpoint only to initialize status. | Missing durable-store configuration can be silently masked by process-local storage. The newer checkpoint is not currently the authoritative AgentContext restoration path. |
| Same `DefaultAgentExecution.java` | `pause()` writes a runtime execution checkpoint and legacy AgentContext checkpoint, then transitions status to PAUSED. `resume()` only transitions PAUSED to RUNNING. `cancel()` only transitions status to CANCELLED. | Pause does not suspend an in-flight model/tool/ReAct operation; resume does not itself restart the loop; cancel does not visibly propagate a cancellation signal to active work. These methods currently implement status/checkpoint controls, not full cooperative lifecycle control. |
| Same `DefaultAgentExecution.java` | The asynchronous ReAct completion callback calls `stateStore.complete`; the error callback calls `stateStore.fail`. | Completion/cancellation and callback races require explicit arbitration and deterministic tests. |
| `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/DefaultExecutionStateStore.java` | `transitionStatus` validates via `ExecutionLifecycleRules.requireTransition` and checks expected state inside `ConcurrentHashMap.compute`. | The expected-state transition path is guarded. |
| Same `DefaultExecutionStateStore.java` | `changeStatus` writes status without transition validation; `fail` directly writes FAILED except when already CANCELLED or COMPLETED. | Separate restore/hydration semantics from live transitions; route live failure through the same transition arbiter. |
| Same `DefaultExecutionStateStore.java` | `checkpoint(executionId, context)` creates and saves a Runtime `ExecutionCheckpoint` containing status, phase, attempt, iteration and AgentContext; updates the in-memory state checkpoint ID; emits a checkpoint event. | Runtime execution checkpoint records are lifecycle-aware, but their AgentContext payload and storage contract remain Runtime-owned. |
| `runtime/andalus-runtime-core/pom.xml` | Runtime-core directly depends on `andalus-execution-api`, `andalus-runtime-spi` and `andalus-engine`. | The runtime compile graph includes the Framework execution API, legacy runtime SPI and engine artifact together. A resolved classpath check is still needed to establish duplicate-FQN behavior in practice. |

### Recovery-path conclusion

The inspected implementation has two checkpoint write paths with different payloads:

1. Runtime `ExecutionCheckpointStore`: stores an `ExecutionCheckpoint` carrying execution lifecycle metadata and an `AgentContext` payload.
2. Framework runtime-SPI `CheckpointStore`: stores and loads `AgentContext` directly and is the source read by `AgentExecutionService.resume`.

The newer store is used to initialize status when a checkpoint is present, but the resume service does not load its AgentContext as the resumed context. The default newer store is in-memory when no CDI implementation is supplied. Therefore the inspected default path does not establish crash-safe resume across process restarts.

The two stores are written sequentially from lifecycle/checkpoint paths; no atomic cross-store transaction or reconciliation protocol is visible in the inspected methods. A partial write can leave the two representations inconsistent.

### Lifecycle findings and required tests

- `pause` is currently checkpoint-plus-status transition, not a safe-point pause protocol.
- `resume` is currently status reactivation, not demonstrated continuation of the suspended ReAct loop.
- `cancel` changes status but does not visibly cancel the active provider stream, tool invocation or loop.
- `fail` bypasses the lifecycle transition validator; `changeStatus` is unguarded and should be restricted to explicit restoration/hydration.
- A late asynchronous completion after cancellation must not overwrite or throw against the terminal outcome. Completion, failure and cancellation need one atomic terminal-outcome arbiter.
- Checkpoint dual-write ordering and partial-failure behavior need explicit policy; prefer one authoritative checkpoint write with secondary projections/events derived from it.

Before changing public contracts or deleting either store, add deterministic tests for: cancel-vs-complete; cancel-vs-tool-return; pause-vs-complete; duplicate/late callbacks; failure from every lifecycle state; checkpoint write failure at each stage; restart/resume from persisted data; and concurrent/duplicate checkpoint writes.

### Framework context ownership correction

The current Framework source already contains `GraphExecutionContext`; the graph engine APIs consume that distinct type. Invocation-scoped `ExecutionContext` remains in `andalus-execution-api` and is implemented by `DefaultExecutionContext` in `andalus-execution-core`. Earlier inventory rows in this document that name `framework/andalus-engine/.../execution/ExecutionContext.java` are stale and should be read as historical inventory, not the current source declaration. Do not perform the graph-context rename a second time. The remaining work is a complete consumer/compatibility scan and reconciliation of all inventory/ADR references.

### Evidence and limitations

Inspected directly in the current Runtime main branch:
- `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/AgentExecutionService.java`
- `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/DefaultAgentExecution.java`
- `runtime/andalus-runtime-core/src/main/java/tech/kayys/andalus/execution/DefaultExecutionStateStore.java`
- `runtime/andalus-runtime-core/pom.xml`

The environment could not resolve `github.com` for a local clone. The GitHub file API allowed inspection of these known paths, but not a complete checkout or exhaustive source search. No Maven dependency tree, clean build, or tests were run. This document therefore records confirmed defects/risks in the inspected methods and leaves full-repository completeness, binary compatibility and runtime behavior under integration tests as open verification items.
