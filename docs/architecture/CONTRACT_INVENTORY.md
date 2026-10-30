# Cross-Repository Contract Inventory

Status: initial source inspection; findings are candidates for design review, not an authorization to delete or rename APIs.

Scope: Andalus Framework and Andalus Runtime source trees inspected on 2026-10-30. This inventory records concrete definitions found and recommends a controlled consolidation.

## Executive summary

The most urgent issue is overlapping public abstractions for execution context and checkpoint storage. In particular, the framework contains two different public interfaces named `tech.kayys.andalus.execution.ExecutionContext` in separate Maven modules. The checkpoint concept is represented by multiple APIs in execution, state and runtime SPI modules. These overlaps create ambiguity for consumers and increase the risk of classpath collisions, inconsistent semantics and duplicated adapters.

Do not perform a flag-day deletion. First establish ownership, map consumers, add conformance tests, then migrate with compatibility adapters.

## Inventory

| Concept | Definition | Current meaning | Assessment |
|---|---|---|---|
| Execution context A | `andalus-execution-api/.../execution/ExecutionContext.java` | Agent invocation metadata, cancellation, security snapshot and lifecycle transition | Good candidate for canonical invocation-level context |
| Execution context B | `andalus-engine/.../execution/ExecutionContext.java` | Graph, variables, resources, current node, parent context and child context | Distinct graph/node execution scope; should be renamed or modeled as a composed/specialized graph context |
| Checkpoint API A | `andalus-execution-api/.../execution/checkpoint/CheckpointStore.java` | Save/get/getLatest/list of `ExecutionCheckpoint` | Strong candidate for canonical execution checkpoint API |
| Checkpoint API B | `andalus-state-api/.../state/checkpoint/CheckpointStore.java` | Save/get/getLatest/list of state `Checkpoint` | State-oriented representation overlaps; determine whether it is a richer envelope or separate abstraction |
| Checkpoint API C | `andalus-runtime-spi/.../execution/CheckpointStore.java` | Save/load/history/delete of `AgentContext` | Legacy/runtime-facing contract; migrate through adapter if consumers remain |
| Checkpoint API D | `andalus-engine/.../execution/CheckpointStore.java` | Save/restore/delete/list of `ExecutionSnapshot` | Engine-local snapshot contract; likely adapt to canonical checkpoint API |
| Lifecycle | `andalus-execution-api/.../execution/ExecutionLifecycle.java` | CREATED, AUTHORIZING, AUTHORIZED, WAITING_OBLIGATION, RUNNING, SUSPENDED, RESUMING, COMPLETED, FAILED, CANCELLED | Explicit invocation lifecycle; preserve as canonical candidate and validate transition table |
| Attempt/phase | `andalus-runtime-core` runtime `ExecutionPhase` and lifecycle record | Processing phase and runtime status/attempt/timestamp | Keep separate from lifecycle status; document dimensions and mapping |

## Key design decisions required

### D1. Execution context ownership

Proposed direction:
- Keep `ExecutionContext` in `andalus-execution-api` as invocation-level context: identity/metadata, cancellation, security and lifecycle.
- Rename the graph-specific interface to `GraphExecutionContext` (or `WorkflowExecutionContext`) in a compatibility-preserving release.
- Keep graph, node, variable and resource access in that specialized interface.
- Avoid returning null for unsupported graph concepts; use a graph-specific type or explicit Optional semantics.
- Define child-context identity, parentage, metadata inheritance and security/cancellation propagation.

### D2. Checkpoint contract ownership

Proposed direction:
- Make `andalus-execution-api` the owner of execution-resumption checkpoints if the primary semantics are attempt, sequence, payload and manifest.
- Decide whether state-api `Checkpoint` is a domain state snapshot, an execution checkpoint envelope, or a separate state persistence abstraction. Avoid two canonical stores with nearly identical CRUD semantics.
- Treat runtime-spi AgentContext checkpointing and engine ExecutionSnapshot storage as legacy/adaptor candidates.
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
- One clearly named graph/workflow execution context contract.
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

## Not yet verified

This inventory is based on the listed source definitions and POM dependencies. It does not yet prove every consumer, implementation, classpath collision in a particular packaging scenario, persistence format, runtime behavior or build outcome. Those require repository-wide import/reference search and CI execution.
