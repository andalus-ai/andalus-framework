# AND-5.3 — Visual Agent and Workflow Designer Contract

## Status

Proposed ecosystem contract, first implementation slice. This document defines the interchange boundary between a visual editor and the existing Andalus workflow model. It does not introduce a frontend framework or a second workflow execution engine.

## Goals

- Make workflows editable by graphical and non-graphical clients.
- Preserve one canonical workflow definition that can be versioned in Git.
- Separate editor-only presentation state from execution semantics.
- Validate a draft before it is submitted to the runtime.
- Keep the designer optional: headless runtime deployments must not depend on it.

## Non-goals

- A browser UI or canvas implementation.
- A new workflow scheduler or execution engine.
- Executing arbitrary expressions supplied by an editor.
- Storing editor layout in runtime execution state.

## Ownership

| Concern | Owner |
|---|---|
| Workflow and node semantics | `andalus-framework` |
| Editor interchange contract and validation rules | `andalus-framework` |
| Draft persistence, publication and execution APIs | `andalus` runtime |
| Browser canvas, forms and collaboration UX | Separate designer application |
| Authentication, authorization and audit | Runtime |

The designer MUST serialize to the canonical Andalus workflow schema. It MUST NOT invent a parallel executable graph format.

## Existing foundation

The framework already contains workflow structures under `andalus-schema-core`, including `WorkflowSpec`, `Connection`, `WorkflowConfig`, and node definitions. The existing `WorkflowSpec` carries nodes, connections, variables, entry/exit identifiers, metadata, and child workflows.

This proposal builds on those structures. In particular, the visual editor's node coordinates and viewport state are presentation metadata, not execution inputs.

## Document model

The framework schema module exposes `WorkflowDocument` as the versioned interchange envelope:

- `schemaVersion`: integer document-schema version (currently `1`).
- `documentId`: stable workflow-document identifier assigned by the caller/runtime.
- `name` and `description`: human-readable document metadata.
- `revision`: non-negative optimistic-concurrency token; revision zero denotes a new, not-yet-persisted document.
- `workflow`: canonical executable workflow definition, represented by the existing `WorkflowSpec`.
- `canvas`: editor presentation data, represented by the existing `CanvasData`.
- `metadata`: extensible document metadata.

The model intentionally reuses `WorkflowSpec` and `CanvasData`; it does not introduce a parallel execution graph. `CanvasData` currently carries visual nodes, edges, layout, groups, and annotations. Runtime persistence owns revision advancement and conflict detection; the framework schema only transports the token.

Example (abbreviated):

```json
{
  "schemaVersion": 1,
  "documentId": "support-triage",
  "name": "Support triage",
  "revision": 0,
  "workflow": {
    "nodes": [],
    "connections": [],
    "variables": {},
    "entryNodeId": "receive",
    "exitNodeIds": ["resolved"]
  },
  "canvas": {
    "nodes": [],
    "edges": [],
    "layout": { "autoLayout": false },
    "groups": [],
    "annotations": []
  },
  "metadata": {}
}
```

The example is illustrative. Executable node fields MUST conform to the actual node schema supported by the selected framework version. Editor-only canvas data MUST NOT be treated as execution instructions.

## Contract requirements

### Workflow identity and versioning

- Every saved workflow document MUST have a stable `documentId`.
- Published workflow versions MUST be immutable.
- Draft revisions MAY change and MUST carry the `revision` token (or an equivalent transport ETag).
- A publish operation MUST record the source revision and framework schema version.
- Importers MUST reject unsupported major schema versions and may warn on newer compatible minor versions.

### Node identity and ports

- Node identifiers MUST be unique within a workflow.
- Connection endpoints MUST reference existing nodes.
- Port identifiers MUST be validated against the node type's declared input/output ports.
- Node type and configuration schemas MUST come from a trusted capability catalog, not from arbitrary UI-provided class names.
- Deleting a node MUST remove or explicitly repair incident connections.

### Editor presentation state

- Canvas coordinates MUST be finite numbers.
- Zoom MUST be finite and within client-defined safe bounds.
- Canvas/editor metadata MUST NOT affect execution semantics.
- Unknown document metadata SHOULD be preserved during round trips to support forward compatibility.
- Runtime execution MUST ignore the `editor` section.

### Validation

The schema module now provides a dependency-free structural validation slice:

- `WorkflowDiagnosticSeverity`: `ERROR`, `WARNING`, or `INFO`.
- `WorkflowDiagnostic`: stable code, severity, JSON pointer, and message.
- `WorkflowValidationResult`: immutable diagnostic list plus validity/error/warning counts.
- `WorkflowDocumentValidator`: checks required document/workflow, supported schema version, node identity uniqueness, entry/exit references, connection endpoints, canvas node identity consistency, and finite canvas coordinates.

Executable node identity is read from `Node.metadata.id`; visual identity is read from `CanvasNode.id`. Missing canvas layout and canvas-only nodes are warnings because visual presentation must not change executable workflow validity. Invalid references in the executable graph and invalid canvas coordinates are errors.

Validation is staged:

1. **Document validation** — parse, required fields, supported schema version.
2. **Structural validation** — unique IDs, valid endpoints, valid ports, entry/exit references.
3. **Semantic validation** — node configuration schemas, required inputs, type compatibility.
4. **Policy validation** — caller authorization, permitted capabilities, secrets and data access.
5. **Runtime readiness** — required plugins/providers available and compatible.

Validation returns machine-readable diagnostics with a stable code, severity, JSON pointer, and human-readable message. A UI may display these diagnostics, but the runtime remains authoritative.

Example diagnostic:

```json
{
  "code": "WORKFLOW_CONNECTION_TARGET_MISSING",
  "severity": "ERROR",
  "pointer": "/workflow/connections/1/toNodeId",
  "message": "Connection references unknown node 'missing-node'."
}
```

### Publication and execution boundary

The designer MUST distinguish saving a draft from publishing and executing it.

Recommended lifecycle:

```text
EDITING -> VALIDATING -> VALIDATED -> PUBLISHED -> EXECUTABLE
                   \-> INVALID
PUBLISHED -> DEPRECATED
```

- Saving a draft MUST NOT execute it.
- Publishing MUST require successful structural and semantic validation.
- Runtime policy checks MUST run again at execution time; publication is not authorization.
- Execution MUST reference an immutable published workflow version.
- Rollback MUST select a prior published version, not mutate historical versions.

## Runtime API shape

The runtime should expose versioned endpoints (exact paths may follow the existing API conventions):

| Operation | Purpose |
|---|---|
| Create draft | Create a workflow document |
| Get draft | Retrieve workflow and editor state |
| Update draft | Save with optimistic concurrency (ETag/revision) |
| Validate | Return structured diagnostics |
| Publish | Create immutable published version |
| List versions | Inspect published history |
| Execute | Execute a specific published version |
| Export | Download portable document |
| Import | Validate and create a draft from a document |

The framework repository owns the data contracts. The runtime repository owns HTTP resources, persistence, authorization, and execution integration.

## Capability catalog

The visual designer needs a discoverable catalog describing which node types can be placed on the canvas. A catalog entry should include:

- Stable capability/type identifier.
- Display name, description, category and icon key.
- Input and output port definitions.
- Configuration JSON Schema (or equivalent schema reference).
- Required permissions.
- Required plugin/provider capability.
- Compatibility range.
- Whether the node supports streaming, retries, or human approval.

Catalog entries are descriptive metadata. The runtime MUST still validate actual configurations and enforce permissions.

## Collaboration and conflict handling

Collaborative editing is intentionally not part of the first contract slice. The initial implementation uses optimistic concurrency:

- Each draft read returns a revision/ETag.
- Updates include the revision they were based on.
- Stale updates return a conflict and do not silently overwrite newer content.

A later collaboration workstream may introduce operation-based synchronization or CRDTs. Do not add CRDT complexity before the single-user draft contract is stable.

## Security requirements

- Treat imported workflow documents as untrusted input.
- Never deserialize arbitrary Java class names from workflow documents.
- Do not place credentials or secret values in workflow definitions.
- Reference secrets through runtime-managed secret identifiers.
- Validate capability permissions at publish and execution time.
- Apply tenant scoping to draft, version, and execution access.
- Audit import, update, validation, publish, and execute operations.
- Sanitize labels and descriptions before rendering them in a browser.

## Compatibility and migration

- Keep the executable `workflow` payload compatible with `WorkflowSpec`.
- Keep editor metadata in a separate top-level `editor` object.
- Older clients that do not understand `editor` may preserve or ignore it without changing workflow execution.
- Schema migrations MUST be explicit and testable.
- Export/import round-trip tests MUST verify that execution semantics are unchanged.

## Implementation sequence

1. Add framework-side `WorkflowDocument` envelope in the schema module, reusing `WorkflowSpec` and `CanvasData`; add diagnostics contracts next.
2. Add schema validation and round-trip tests (the initial envelope round-trip tests are included with the model).
3. Add runtime draft/version persistence and optimistic concurrency.
4. Add runtime validate/publish/export/import APIs.
5. Implement the browser designer as a separate application consuming the runtime APIs and capability catalog.
6. Add execution preview and debugging against published versions.
7. Add collaboration only after revision conflict behavior is proven.

## Definition of done

- A document can round-trip through JSON without losing workflow semantics or editor metadata.
- Editor coordinates and viewport never affect execution.
- Invalid references and ports produce stable diagnostics.
- Draft updates detect stale revisions.
- Published versions are immutable.
- Execution targets an explicit published version.
- Imported documents are treated as untrusted and validated.
- The runtime works without the visual designer being installed.
