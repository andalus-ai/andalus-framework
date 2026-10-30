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
| Engine snapshot store | `framework/andalus-engine/.../execution/CheckpointStore.java` | No implementation confirmed in the inspected tree paths | Saves/restores engine `ExecutionSnapshot`; `getLatest` default returns null |
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
- The duplicate `ExecutionContext` FQN is therefore a cross-artifact concern, not two declarations in one source module.

A resolved Maven dependency tree and clean build are required to establish the actual compiler/classloader behavior.

## 4. Consumer map status

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

## 5. ExecutionContext ownership decision record

A proposed decision record now captures the recommended separation of invocation and graph execution scopes, rejected alternatives, required migration checks and compatibility risks: [ADR-0001 — Separate Invocation and Graph Execution Contexts](ADR-0001-execution-context-ownership.md). The ADR does not authorize a source rename; the repository-wide reference map and compatibility checks remain prerequisites.

## 6. Recommended decisions before source migration

1. Name the graph context distinctly, e.g. `GraphExecutionContext`, and reserve `ExecutionContext` for invocation-level context only if that ownership decision is approved.
2. Decide the canonical durable execution checkpoint envelope and document the role of state snapshots.
3. Deprecate old contracts only after adapters and compatibility tests exist.
4. Enforce lifecycle transitions in `DefaultExecutionContext`.
5. Make checkpoint sequence writes atomic per execution and define duplicate/idempotency behavior.
6. Add cross-module contract tests and build checks that detect duplicate public FQNs across Maven artifacts.

## Scope and limitations

This is a targeted source map, not a complete static-analysis report. The repository search connector did not provide a reliable complete code-reference index for these queries. All "not yet confirmed" items must be resolved by local grep/IDE indexing or a full source checkout before destructive API changes.
