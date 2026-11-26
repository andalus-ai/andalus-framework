# Cross-Repository Contract Inventory

Status: refreshed source inventory as of 2026-11-25. Findings are evidence for design review, not authorization to delete or rename public APIs.

Scope: current default-branch declarations and selected implementations in Andalus Framework and Runtime. The detailed evidence log is [M0 Foundation Audit](M0-FOUNDATION-AUDIT.md); this file is the concise current contract index.

## Executive summary

The earlier duplicate `tech.kayys.andalus.execution.ExecutionContext` declaration has been resolved in current Framework source: invocation scope is `ExecutionContext`, while graph/node scope is `GraphExecutionContext`. Checkpointing still has several distinct contracts for different payloads and recovery boundaries; this is a semantic ownership and adapter question, not a confirmed same-FQN collision. Runtime currently maintains additional AgentContext and lifecycle checkpoint paths.

Do not perform a flag-day deletion. First establish ownership, map consumers, add conformance tests, then migrate with compatibility adapters.

## Inventory

| Concept | Definition | Current meaning | Assessment |
|---|---|---|---|
| Execution context A | `andalus-execution-api/.../execution/ExecutionContext.java` | Agent invocation metadata, cancellation, security snapshot and lifecycle transition | Good candidate for canonical invocation-level context |
| Graph execution context | `andalus-engine/.../execution/GraphExecutionContext.java` | Graph, variables, resources, current node, parent context and child context | Distinct graph/node scope; rename is already present in current source |
| Checkpoint API A | `andalus-execution-api/.../execution/checkpoint/CheckpointStore.java` | Save/get/getLatest/list of `ExecutionCheckpoint` | Strong candidate for canonical execution checkpoint API |
| Checkpoint API B | `andalus-state-api/.../state/checkpoint/CheckpointStore.java` | Save/get/getLatest/list of state `Checkpoint` | State-oriented representation overlaps; determine whether it is a richer envelope or separate abstraction |
| Checkpoint API C | `andalus-runtime-spi/.../execution/CheckpointStore.java` | Save/load/history/delete of `AgentContext` | Legacy/runtime-facing contract; migrate through adapter if consumers remain |
| Engine snapshot API | `andalus-engine/.../engine/snapshot/EngineSnapshotStore.java` | Save/restore/delete/list of `ExecutionSnapshot` | Engine-local snapshot contract with a distinct FQN; not yet implemented by an in-repository adapter |
| Lifecycle | `andalus-execution-api/.../execution/ExecutionLifecycle.java` | CREATED, AUTHORIZING, AUTHORIZED, WAITING_OBLIGATION, RUNNING, SUSPENDED, RESUMING, COMPLETED, FAILED, CANCELLED | Explicit invocation lifecycle; preserve as canonical candidate and validate transition table |
| Attempt/phase | `andalus-runtime-core` runtime `ExecutionPhase` and lifecycle record | Processing phase and runtime status/attempt/timestamp | Keep separate from lifecycle status; document dimensions and mapping |

## Key design decisions required

### D1. Execution context ownership

Proposed direction:
- Keep `ExecutionContext` in `andalus-execution-api` as invocation-level context: identity/metadata, cancellation, security and lifecycle.
- Keep graph-specific operations on the existing `GraphExecutionContext`; do not repeat the already completed rename.
- Keep graph, node, variable and resource access in that specialized interface.
- Avoid returning null for unsupported graph concepts; use a graph-specific type or explicit Optional semantics.
- Define child-context identity, parentage, metadata inheritance and security/cancellation propagation.

### D2. Checkpoint contract ownership

Proposed direction:
- Make `andalus-execution-api` the owner of execution-resumption checkpoints if the primary semantics are attempt, sequence, payload and manifest.
- Decide whether state-api `Checkpoint` is a domain state snapshot, an execution checkpoint envelope, or a separate state persistence abstraction. Avoid two canonical stores with nearly identical CRUD semantics.
- Treat runtime-spi AgentContext checkpointing as a legacy/adaptor candidate. Keep engine `ExecutionSnapshot` storage explicitly engine-scoped unless a lossless mapping to `ExecutionCheckpoint` is proven.
- Define atomicity, ordering, idempotency, retention, serialization format, schema versioning and corruption behavior.
- Avoid default methods that silently return null for a checkpoint lookup; use Optional or explicit unsupported behavior.

### D3. Lifecycle and phase

Proposed direction:
- Keep lifecycle status (e.g. RUNNING, SUSPENDED, COMPLETED) distinct from execution phase (e.g. PLANNING, TOOL, MEMORY).
- Define legal transitions in one reusable state machine or transition policy.
- Define whether transition operations are persisted and evented by the runtime, while keeping transition semantics in the framework.
- Define attempt count separately from lifecycle state and processing phase.

## Required consumer/implementation mapping

Before API changes, complete a repository-wide mapping for:
- All implementations and call sites of each interface.
- CDI producers/injectable beans and service-loader registrations.
- Runtime modules and external examples consuming each artifact.
- Serialized checkpoint formats and persisted records.
- Tests, fixtures and test-only implementations.
- Public release/artifact compatibility constraints.

Search must include fully qualified type names and imports, not just simple class names. Record every consumer in the follow-up migration issue or pull request.

## Acceptance criteria for consolidation

- One canonical invocation-level ExecutionContext FQN.
- A distinct `GraphExecutionContext` for graph/workflow operations (present in current source).
- One canonical execution checkpoint store contract, with explicit state snapshot relationship.
- No accidental duplicate public FQNs across artifacts.
- No null-returning default methods for required lookup operations.
- Contract tests cover legal/illegal lifecycle transitions, checkpoint ordering, duplicate saves, missing checkpoints, schema compatibility and cancellation propagation.
- Runtime adapters pass the framework conformance suite.
- Both repositories build and their compatibility matrix is documented.

## Migration sequence

1. Freeze new competing APIs; add deprecation notices where safe.
2. Complete consumer and implementation mapping.
3. Approve canonical ownership and naming through an ADR.
4. Add adapters and contract/conformance tests.
5. Migrate runtime modules incrementally.
6. Deprecate legacy APIs for at least one planned compatibility window.
7. Remove only in a documented breaking release.

## Current source snapshot — 2026-11-25

- `framework/andalus-engine/.../GraphExecutionContext.java` is the current graph context declaration; the old graph `ExecutionContext` declaration is no longer present at that path.
- `framework/andalus-execution-core/.../DefaultExecutionContext.java` validates lifecycle transitions using `ExecutionLifecycleTransitions` in a compare-and-set loop. Same-state transitions are idempotent; illegal transitions throw `IllegalStateException`.
- `framework/andalus-execution-core/.../checkpoint/InMemoryExecutionCheckpointStore.java` synchronizes sequence validation, duplicate-ID insertion and history append on a per-execution history object. This improves concurrent ordering but remains process-local and is not durable storage.
- `EngineSnapshotStore` is now under `tech.kayys.andalus.engine.snapshot`; it is distinct from the invocation checkpoint API and runtime-SPI AgentContext store.
- Additional lifecycle abstractions (`ExecutionStateMachine` and `ExecutionLifecycleState`) remain in the Framework tree. Their relationship to invocation lifecycle and harness durable control state needs a complete consumer map before consolidation.

## Not yet verified

This inventory is based on the listed source definitions and POM dependencies. It does not yet prove every consumer, implementation, classpath collision in a particular packaging scenario, persistence format, runtime behavior or build outcome. Those require repository-wide import/reference search and CI execution.
