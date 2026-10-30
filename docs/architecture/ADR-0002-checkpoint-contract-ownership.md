# ADR-0002: Establish Checkpoint Contract Ownership

- **Status:** Proposed
- **Date:** 2026-10-30
- **Decision owners:** Framework and runtime maintainers
- **Related documents:** [Contract inventory](CONTRACT_INVENTORY.md), [Contract consumer map](CONTRACT_CONSUMER_MAP.md), [ADR-0001](ADR-0001-execution-context-ownership.md)

## Context

The framework currently exposes four checkpoint-store contracts with overlapping names but different payloads and semantics:

| Artifact | Fully qualified name | Payload / responsibility |
|---|---|---|
| `andalus-engine` | `tech.kayys.andalus.execution.CheckpointStore` | Stores and restores engine `ExecutionSnapshot` values |
| `andalus-runtime-spi` | `tech.kayys.andalus.execution.CheckpointStore` | Saves and loads runtime `AgentContext` values |
| `andalus-execution-api` | `tech.kayys.andalus.execution.checkpoint.CheckpointStore` | Stores `ExecutionCheckpoint` records by checkpoint and execution ID |
| `andalus-state-api` | `tech.kayys.andalus.state.checkpoint.CheckpointStore` | Stores state-layer `Checkpoint` records |

The first two contracts publish the same fully qualified name from different Maven artifacts. They are not binary-compatible: one accepts `UUID` and `ExecutionSnapshot`; the other accepts `String` and `AgentContext`. This is an API ownership and classpath ambiguity that must be resolved independently of the execution-context collision documented in ADR-0001.

The runtime currently uses two recovery paths with different payloads and purposes: a runtime-SPI store persists semantic `AgentContext` snapshots, while `ExecutionCheckpointStore` persists execution checkpoint records through `DefaultExecutionStateStore`. These paths must not be collapsed until their recovery guarantees, retention semantics and consumers are understood.

## Decision proposed

1. **Execution checkpoint records:** Treat `andalus-execution-api`'s `tech.kayys.andalus.execution.checkpoint.CheckpointStore` as the preferred framework-facing port for durable execution checkpoint records. It should own checkpoint identity, lookup, per-execution history and latest-checkpoint retrieval.
2. **Generic state checkpoints:** Keep `andalus-state-api`'s `tech.kayys.andalus.state.checkpoint.CheckpointStore` scoped to state-layer snapshots. It is not an alias for the execution checkpoint port; state checkpoints may have different identity, consistency and lifecycle requirements.
3. **Engine snapshot contract:** The engine must not publish a second `tech.kayys.andalus.execution.CheckpointStore`. Replace its dependency with the execution checkpoint port or a distinctly named engine-specific adapter contract only if engine snapshot semantics cannot be represented by the execution checkpoint model.
4. **AgentContext recovery:** The runtime-SPI contract must move out of the colliding `tech.kayys.andalus.execution` package and receive a name that states its payload and scope (for example, `AgentContextSnapshotStore` in a runtime-owned package). It may remain as a compatibility adapter while the runtime migrates to the canonical execution checkpoint flow.
5. **No payload conflation:** Do not assume `ExecutionSnapshot`, `AgentContext`, `ExecutionCheckpoint` and state `Checkpoint` are interchangeable. Define explicit mapping/serialization boundaries and document which data each recovery path guarantees.
6. **No same-FQN compatibility alias:** A deprecated alias with the colliding `tech.kayys.andalus.execution.CheckpointStore` FQN is not an acceptable migration strategy. Any compatibility bridge must use a distinct package and type name.
7. **Migration is coordinated:** Update framework implementations, runtime adapters, dependency declarations, tests, examples and extension points together. Treat public binary compatibility as an explicit versioning decision.

## Alternatives considered

### Merge all checkpoint contracts into one interface

Rejected. Engine snapshots, semantic agent context, execution checkpoint envelopes and generic state snapshots may have different consistency, retention, serialization and recovery requirements. A single broad interface would couple unrelated modules and obscure those guarantees.

### Keep duplicate FQNs and rely on Maven exclusions

Rejected as a durable solution. Consumers that legitimately depend on both artifacts remain exposed to ambiguous class definitions, and exclusions can silently remove required functionality.

### Retain parallel runtime recovery paths indefinitely

Rejected as a default architecture. Parallel paths create uncertainty about which snapshot is authoritative during pause, completion and resume. A temporary coexistence period is acceptable only with documented ownership, ordering and reconciliation behavior.

## Required investigation before implementation

- [ ] Map every declaration, implementation, injection point and call site for all four contracts across both repositories.
- [ ] Inspect all checkpoint models, serializers, storage adapters and schema/versioning behavior.
- [ ] Document exactly which runtime events write each store and which resume path reads it.
- [ ] Establish whether engine `ExecutionSnapshot` can be represented by `ExecutionCheckpoint` without losing graph-specific recovery data.
- [ ] Define checkpoint ordering, idempotency, uniqueness, retention, deletion and concurrency guarantees for each port.
- [ ] Decide whether `AgentContextSnapshotStore` is temporary compatibility infrastructure or a separately supported runtime capability.
- [ ] Add contract tests for save/load round trips, latest ordering, concurrent writers, duplicate IDs, deletion and schema evolution.
- [ ] Add dependency/classpath verification proving no duplicate public FQNs remain in resolved runtime artifacts.
- [ ] Run clean framework and runtime Maven builds and relevant module/integration tests.
- [ ] Publish a migration and compatibility guide before removing or changing public contracts.

## Consequences

### Positive

- Each checkpoint port has an explicit owner and payload boundary.
- Engine and runtime modules can evolve without accidentally sharing incompatible public types.
- The runtime can converge on one authoritative execution-recovery flow where the semantics genuinely overlap.
- State-layer persistence remains independent where its guarantees differ.

### Costs and risks

- A coordinated API migration is required, potentially across a major version boundary.
- Existing adapters may need explicit conversion code and temporary compatibility bridges.
- The canonical execution checkpoint model may need additional fields or versioning support after the model audit.
- Until the runtime write/read paths are fully mapped, removing either existing recovery path could cause data loss or break resume behavior.

## Current implementation status

This ADR records a proposed ownership model and investigation checklist. No checkpoint interface, payload model or runtime recovery path has been changed by this document. The migration remains blocked on the repository-wide consumer map, payload/serialization comparison, recovery-flow tests and clean framework/runtime builds.
