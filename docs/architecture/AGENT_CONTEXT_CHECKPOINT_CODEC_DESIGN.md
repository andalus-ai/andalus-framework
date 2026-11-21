# AgentContext Checkpoint Codec Design

## Status

Proposed design; no runtime behavior or public API is changed by this document.

## Context

The canonical durable checkpoint contract is `tech.kayys.andalus.execution.checkpoint.CheckpointStore` in `andalus-execution-api`. It stores an `ExecutionCheckpoint` containing an opaque byte payload, `AttemptId`, per-execution sequence, timestamp, manifest and integrity checksum. The Framework contract owns atomic append/duplicate/stale-sequence semantics; a persistence adapter owns the actual durability guarantee.

Runtime recovery currently needs to preserve the Framework `AgentContext`. It is not a serialization DTO and must not be passed directly to a generic object mapper as the durable wire format.

## Confirmed AgentContext state inventory

The current `AgentContext` source declares the following state:

| Field | Shape | Recovery concern |
|---|---|---|
| `id` | `ResourceId` | Polymorphic identity; preserve subtype and underlying ID exactly |
| `sessionId`, `tenantId`, `namespace` | nullable strings | Preserve null vs empty and tenant/session scope |
| `request` | `AgentRequest` record | Contains polymorphic `InputType`, artifact list, metadata and parameter maps |
| `response` | `AgentResponse` record | Contains artifacts, metadata/data maps, success/error and response timing |
| `variables`, `attributes` | `Map<String,Object>` | Arbitrary runtime values; requires explicit value codec registry |
| `artifacts` | `List<Artifact>` | Interface/polymorphic values; payload may be external or large |
| `contextData` | `ContextData` record | Includes documents, arbitrary memories/knowledge, structured data and metadata |
| `outputResult` | `OutputResult` | Type and reconstruction contract must be inspected before implementation |
| `state`, `currentPhase` | enums | Preserve exact enum token; define unknown-version behavior |
| `createdAt`, `updatedAt` | `Instant` | Preserve exactly; do not regenerate on restore |
| `version` | integer | Preserve exactly |
| `metadata` | `Map<String,String>` | Preserve all entries |
| `startTime`, `elapsedTime` | long | Preserve elapsed execution accounting; do not treat wall-clock duration as authoritative |
| `phaseTimings` | `Map<String,Long>` | Preserve all recorded phase durations |
| `completedPhases` | `Set<String>` | Preserve insertion order if relied upon by consumers |
| `history` | `List<AgentContext>` | Recursive snapshots can duplicate the full prior history and grow quadratically |

The current source does **not** declare a principal field, despite a getter comment describing one. Principal/identity data must therefore come from the execution checkpoint envelope or the runtime lifecycle metadata, not from an imagined `AgentContext.principal()` accessor.

## Serialization hazards

1. **Arbitrary object graphs.** Variables, attributes, request/response maps, context memories/knowledge and structured data are `Object`-valued. Jackson default typing or Java native serialization would create compatibility and security risks.
2. **Polymorphic contracts.** `ResourceId`, `Artifact`, `InputType` and `OutputResult` require explicit subtype/type handling. The codec must not serialize implementation class names and later instantiate arbitrary classes.
3. **External payloads.** Artifact/document content may be binary or large. The durable context payload should carry stable artifact/resource references and descriptors where supported, not silently drop content or inline unbounded blobs.
4. **Recursive history.** Each new context can append the previous context, which itself contains its own history. Serializing the object graph naively can cause payload growth and cycles if future implementations introduce back references.
5. **Mutable value containers.** Constructor copies are shallow for `variables`, `attributes`, and nested record maps/lists. Snapshot encoding must capture a consistent point-in-time value and must not retain mutable references.
6. **Restore defaults.** `AgentContext.Builder` generates an ID and timestamps when omitted. A decoder must explicitly restore every field so these defaults never replace persisted identity/times.
7. **Documentation mismatch.** The class documentation says immutable-by-default, but shallow copies and mutable nested values mean the codec cannot assume deep immutability.

## Proposed boundary and ownership

- Keep the canonical checkpoint envelope and store contract in `andalus-execution-api`.
- Keep recovery policy, attempt/sequence allocation, lifecycle transitions and orchestration in Runtime.
- Implement the `AgentContext` snapshot codec at the Runtime integration boundary (or a dedicated Runtime persistence module), not in the low-level Framework execution API.
- Use a versioned, explicit snapshot DTO. The codec maps `AgentContext` to/from that DTO and a deterministic byte representation.
- Do not add a second checkpoint envelope/commit interface. Reuse `ExecutionCheckpoint`, `CheckpointManifest`, `CheckpointIntegrity` and `AttemptId`.
- Keep the legacy `AgentContext` checkpoint store as a read fallback during migration. Do not remove legacy writes until restore parity and rollback behavior are demonstrated.

## Snapshot DTO requirements

The DTO must carry:

- schema identifier and schema version;
- every field in the inventory above, including nullability and exact timestamps;
- explicit tagged values for supported variable/attribute/map values;
- artifact/document descriptors and stable external references, with a resolver contract for external bytes;
- explicit subtype tags for identity/input/artifact/output values;
- history as deduplicated, ordered historical snapshot references (or an equivalent normalized representation), not recursively duplicated object graphs;
- execution identity, attempt identity, sequence, tenant/user/principal metadata and cache/resource references in the checkpoint envelope/manifest where appropriate.

Unknown tagged value types must fail encoding with a typed error or be handled by an explicitly configured extension codec. They must never be stringified, dropped, or deserialized through unrestricted class loading.

## Required validation before implementation

1. Inventory all concrete `Artifact`, `InputType`, `OutputResult`, `ResourceId`, and document representations used by current execution paths.
2. Inventory real value types stored in `variables`, `attributes`, `AgentRequest` maps, `AgentResponse` maps, and `ContextData` maps/lists.
3. Define a safe, versioned type registry and unknown-type policy.
4. Define artifact externalization, reference resolution, missing-resource behavior and retention guarantees.
5. Define history normalization and migration of existing nested history.
6. Define payload size limits, compression policy and deterministic encoding.
7. Verify that a decoded context restores exact IDs, timestamps, version, lifecycle state, phase data, metadata, request/response, output and artifacts.

## Acceptance tests

- Round-trip every field, including null values, empty collections, enum values, timestamps and ordering-sensitive collections.
- Round-trip representative supported nested value types and reject an unregistered type without data loss.
- Round-trip artifacts and documents with both inline and externally referenced content.
- Round-trip a multi-version history without recursive duplication or loss of order.
- Reject unsupported schema versions and malformed type tags deterministically.
- Verify checksum failure prevents decode.
- Verify replay of an already committed checkpoint is idempotently recognized by the Runtime adapter, while stale sequence writes are rejected by the canonical store.
- Verify a failed checkpoint write does not report a successful pause or leave persisted lifecycle state ahead of its checkpoint.
- Verify restart recovery preserves tenant, user, principal, attempt identity, sequence and cache/resource references.

## Non-goals

- This design does not promise that in-memory stores survive process restarts.
- This design does not make arbitrary Java objects durable.
- This design does not change the public `AgentContext` API or remove legacy checkpoint APIs.
- This design does not claim atomicity across separate stores; that requires a transactional persistence adapter or a single authoritative persistence operation.
