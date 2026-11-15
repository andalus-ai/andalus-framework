# ADR-0003: Define Execution Lifecycle, Cancellation and Resume Semantics

- **Status:** Proposed
- **Date:** 2026-11-15
- **Decision owners:** Framework and Runtime maintainers
- **Related decisions:** [ADR-0001 — Execution context ownership](ADR-0001-execution-context-ownership.md), [ADR-0002 — Checkpoint contract ownership](ADR-0002-checkpoint-contract-ownership.md)
- **Scope:** Portable execution semantics in Framework and their orchestration/implementation in Runtime

## Context

The current Runtime implementation has an explicit lifecycle transition table and a per-execution state store, but the inspected execution path does not yet make lifecycle state authoritative over the work being performed.

The source audit identified these issues:

1. Failure handling can write `FAILED` without passing through the lifecycle transition rules.
2. A general status mutation method can bypass transition validation; restore/hydration is not clearly separated from live transitions.
3. Pause updates status and writes checkpoints but does not suspend the active ReAct/model/tool operation.
4. Cancel updates status but does not propagate cancellation to model streams, tool execution or specialist loops.
5. Late asynchronous completion can race with cancellation and attempt an invalid terminal transition.
6. Resume restores an `AgentContext` from the legacy store and marks execution running, but the inspected path does not demonstrate continuation from a well-defined checkpoint phase.
7. The runtime writes both an execution lifecycle checkpoint and a legacy AgentContext snapshot. Sequential dual writes can diverge if one succeeds and the other fails.
8. Default in-memory stores can make an execution appear checkpoint-enabled without providing process-restart durability.

These findings are source-level observations from the current repository paths. The relevant tests were inspected but not executed. This ADR therefore defines target semantics; it does not claim that current code already conforms.

Checkpoint contract ownership and payload migration are addressed separately in ADR-0002. This ADR defines how the lifecycle uses that checkpoint capability.

## Decision proposed

### 1. Ownership boundary

**Framework owns portable execution semantics and contracts. Runtime owns live lifecycle orchestration and concrete execution infrastructure.**

Framework contracts should define the portable meaning and shape of:

- Execution identity and invocation context.
- Lifecycle states and legal transition semantics, or a shared conformance specification where a shared enum would create undesirable coupling.
- Cancellation and deadline propagation contracts.
- Checkpoint envelope requirements and resume metadata.
- Progress and terminal outcome semantics visible to framework consumers.

Runtime is responsible for:

- Creating and arbitrating live executions.
- Enforcing lifecycle transitions atomically.
- Connecting cancellation to model providers, tool executors and specialist-agent loops.
- Coordinating checkpoint persistence and restoration.
- Providing concrete storage adapters, scheduling and process lifecycle behavior.
- Reporting whether pause, cancellation and durable resume are actually supported by the configured providers and stores.

A domain product such as Cifr or Farabi remains authoritative for its own domain task state and internal specialist loop. Runtime lifecycle state describes the bounded agent execution, not the product's canonical business workflow.

### 2. One lifecycle transition arbiter

Every live state mutation MUST pass through one per-execution transition arbiter. This includes success, failure, cancellation, pause and resume.

The arbiter MUST:

- Read and validate the current state and requested transition atomically.
- Reject illegal transitions consistently.
- Ensure exactly one terminal outcome wins.
- Make duplicate or late callbacks idempotent where possible.
- Publish a stable outcome to the execution future/caller without allowing callback exceptions to leave the execution unresolved.
- Record transition metadata suitable for audit and diagnostics.

No implementation path may directly assign a live status as a shortcut around the arbiter.

Checkpoint hydration is not a live transition. It MUST use a separately named, restricted restoration operation that validates checkpoint identity, schema, integrity and allowed recovery state before installing state. It must not be exposed as a general-purpose status setter.

### 3. Cancellation is cooperative and end-to-end

Cancellation MUST be represented by a cancellation signal/token associated with the execution and propagated through:

`invocation → orchestration loop → model request/stream → tool call → specialist-agent invocation`

The contract SHOULD support:

- A cancellation reason and request timestamp.
- Idempotent cancellation requests.
- Deadline expiry as a cancellation trigger with a distinguishable reason.
- Provider/tool capability reporting where immediate interruption is unavailable.
- Safe-point checks before starting additional model/tool work.
- Best-effort interruption of in-flight operations where the underlying provider supports it.

A cancelled execution MUST NOT later become completed or failed because of a late callback. The arbiter should preserve the winning terminal outcome and record any late result as diagnostic information rather than changing the outcome.

Cancellation does not imply rollback of external side effects already performed by a tool. Tool idempotency, compensation and side-effect policy remain explicit tool/domain concerns.

### 4. Pause means cooperative suspension, not a status label

Pause MUST be defined as a cooperative request to suspend at a safe point.

A pause request should:

1. Signal the active execution to stop starting new work.
2. Allow or request the active operation to reach a safe point, subject to a configured deadline.
3. Persist the authoritative checkpoint required for continuation.
4. Transition to PAUSED only after the checkpoint is committed and the execution is no longer advancing.
5. Return an explicit unsupported/failed result if a provider or tool cannot reach a safe point within the allowed policy.

The system MUST NOT report PAUSED while model/tool work continues to mutate execution state in the background.

If true suspension is not supported for a particular execution mode, the API must expose that limitation. It must not silently degrade pause into a status-only update.

### 5. Resume means restoration and continuation

Resume MUST mean more than changing PAUSED to RUNNING.

A resumable checkpoint must identify enough state to continue deterministically within the guarantees of the execution mode, including as applicable:

- Execution and checkpoint IDs.
- Monotonic checkpoint sequence and schema version.
- Lifecycle state and phase/stage.
- Attempt and iteration information.
- Versioned serialized execution context payload.
- Pending/active operation information or a safe-point marker.
- References to required cache, artifact and event-ledger data.
- Integrity/manifest metadata and creation time.

Resume MUST load and validate the authoritative checkpoint, hydrate the execution, re-establish cancellation/deadline context, and continue from a documented phase. If the checkpoint lacks required information or its schema cannot be migrated, resume must fail explicitly with a typed error; it must not fabricate a successful continuation by merely setting RUNNING.

The Framework execution checkpoint envelope is the preferred portable persistence contract identified by ADR-0002. Runtime may serialize `AgentContext` as a versioned payload within that envelope. The exact payload schema and storage adapter are implementation decisions to be settled after the consumer and serializer inventory.

### 6. One authoritative checkpoint write path

For a given execution, one checkpoint record/path MUST be authoritative for lifecycle restoration.

Secondary data such as event ledgers, cache references, tracing events and compatibility snapshots must be treated as derived or separately versioned records with explicit consistency rules.

Preferred direction:

- Commit the authoritative checkpoint using the Framework execution checkpoint port.
- Include versioned execution context and references required for recovery.
- Derive secondary views/events from the authoritative checkpoint, or use an outbox/reconciliation mechanism where atomic cross-store transactions are unavailable.
- Do not sequentially dual-write two independent stores and assume they remain consistent.

During migration, a compatibility bridge may dual-read or dual-write temporarily only when ordering, failure handling, reconciliation, observability and a removal condition are documented. It must not become a permanent second source of truth.

### 7. Durability must be explicit

The runtime must distinguish at least:

- **In-memory execution:** no process-restart recovery guarantee.
- **Checkpointed execution:** checkpoint records are produced, but durability depends on the configured store.
- **Durable resumable execution:** the authoritative checkpoint and all referenced recovery data survive the declared failure model and can be restored by a supported runtime.

The configured storage capability must be visible in execution metadata or the returned capability/result. An implicit in-memory fallback MUST NOT be represented as durable checkpointing. Production configurations that require durable resume should fail fast when no durable store is configured.

### 8. State machine baseline

The implementation must define and test one explicit transition table. The following is the intended baseline; exact intermediate states may be added only with documented semantics.

| Current state | Requested state | Expected behavior |
|---|---|---|
| PENDING | RUNNING | Start execution |
| PENDING | CANCELLED | Cancel before work starts |
| RUNNING | PAUSING | Begin cooperative safe-point pause |
| PAUSING | PAUSED | Checkpoint committed and work suspended |
| PAUSING | RUNNING | Pause request aborted before suspension |
| RUNNING | COMPLETED | Successful terminal outcome |
| RUNNING | FAILED | Failed terminal outcome |
| RUNNING | CANCELLED | Cancellation accepted; active work signalled |
| PAUSED | RUNNING | Restore checkpoint and continue |
| PAUSED | CANCELLED | Cancel suspended execution |
| COMPLETED / FAILED / CANCELLED | any different state | Reject; terminal outcome is immutable |

If the existing public state enum cannot represent PAUSING, the implementation may use an internal transition phase or introduce the state through a compatibility-reviewed API change. It must not claim a fully cooperative pause without representing the in-progress pause operation.

## Alternatives considered

### Treat pause and cancel as status changes

Rejected. This reports a lifecycle state that does not correspond to actual work execution, permits background work after cancellation and makes resume semantics ambiguous.

### Let each callback update lifecycle state independently

Rejected. Multiple completion, failure and cancellation callbacks can race, producing contradictory terminal outcomes or unresolved futures.

### Keep both checkpoint stores as equal sources of truth

Rejected. Dual writes are not atomic by default and resume currently reads a different store from the one that carries the richer lifecycle metadata.

### Make Framework own all runtime lifecycle implementation

Rejected. Framework should define portable contracts and semantics, not concrete process orchestration, provider interruption, scheduling or deployment-specific persistence.

## Required implementation and verification work

- [ ] Map every lifecycle status mutation and route it through the transition arbiter.
- [ ] Separate checkpoint hydration from live lifecycle transitions.
- [ ] Define cancellation/deadline propagation interfaces and adapt model, tool and specialist-agent execution.
- [ ] Define safe-point behavior for each supported model/tool execution mode.
- [ ] Define the resumable checkpoint payload schema and its versioning/migration policy.
- [ ] Select the authoritative checkpoint write/read path and document cache/event-ledger consistency.
- [ ] Remove implicit in-memory fallback for configurations that require durability; expose capability levels.
- [ ] Add deterministic tests for every allowed and rejected transition.
- [ ] Add race tests: cancel-vs-complete, cancel-vs-fail, cancel-vs-tool-return, pause-vs-complete, duplicate callbacks and deadline-vs-terminal callback.
- [ ] Add tests proving no work continues after PAUSED or CANCELLED is reported.
- [ ] Add process-restart integration tests proving durable resume from the authoritative checkpoint.
- [ ] Add fault-injection tests for checkpoint write failure, partial secondary-store failure and recovery reconciliation.
- [ ] Run Framework and Runtime clean builds and relevant module/integration tests; record exact JDK, Framework revision and Runtime revision.

## Migration sequence

1. **Contract inventory:** complete the Framework/Runtime lifecycle and checkpoint consumer map, including serializers, CDI injection points, external extension APIs and cache/event references.
2. **Conformance tests:** codify current intended state transitions and terminal-outcome arbitration before changing implementation.
3. **Framework contract:** finalize the portable cancellation/deadline and checkpoint envelope requirements, coordinating with ADR-0002. Avoid introducing a second checkpoint abstraction.
4. **Runtime arbiter:** route all live transitions through one validated, atomic state transition component.
5. **Cancellation propagation:** add end-to-end cancellation and safe-point pause behavior with explicit provider/tool capability reporting.
6. **Checkpoint adapter:** serialize versioned `AgentContext` and required resume metadata into the authoritative Framework checkpoint envelope.
7. **Compatibility bridge:** support existing checkpoint data through explicit adapters and a bounded dual-read/migration period; instrument which path is used.
8. **Single-writer cutover:** make the canonical path the only authoritative writer after reconciliation and restart tests pass.
9. **Deprecation/removal:** remove duplicate FQN contracts and legacy stores only after external consumer checks, compatibility tests and release/versioning policy are complete.

Do not combine this migration with unrelated module renames or broad dependency restructuring.

## Consequences

### Positive

- Lifecycle status reflects actual execution behavior.
- Cancellation and deadlines can stop additional model/tool work instead of only changing metadata.
- Pause/resume semantics become testable and honest about provider limitations.
- One terminal outcome and one authoritative recovery path reduce race conditions and inconsistent restores.
- Framework remains portable while Runtime retains concrete orchestration and storage responsibility.
- Cifr, Farabi and other domain products can integrate without transferring their domain workflow ownership to Andalus.

### Costs and risks

- Cancellation and safe-point support require changes across model, tool and agent-loop boundaries.
- Some providers/tools cannot be interrupted immediately; the contract must report bounded best-effort behavior.
- Versioned context serialization and migration require explicit compatibility work.
- Durable resume requires storage and artifact/cache references to satisfy a declared failure model.
- The transition arbiter and checkpoint cutover need concurrency and process-restart tests.

## Current implementation status

This ADR is a proposed target architecture. It does not change Framework or Runtime source code and does not assert implementation conformance. Implementation is blocked on the required consumer/payload inventory, contract tests, coordinated approval with ADR-0002 and an explicit release compatibility plan.
