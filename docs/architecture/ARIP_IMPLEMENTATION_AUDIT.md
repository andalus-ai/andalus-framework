# ARIP implementation audit — current Andalus API surface

**Audit date:** 2026-11-26  
**Scope:** Read-only source inspection of the current `andalus-ai/andalus` and `andalus-ai/andalus-framework` main branches  
**Purpose:** Ground ARIP design in existing APIs before proposing implementation changes.

This is an API-surface audit, not a claim that every service is fully wired, secured, durable, or production-ready. The listed source files should be inspected with their CDI wiring, security configuration, persistence implementations and tests before relying on runtime behavior.

## 1. Existing Framework contracts

In `andalus-framework/framework/andalus-communication-api`:
- `AgentCommunicator` already exposes `send`, `submit`, and `stream`.
- `AgentRequest` carries target, capability, payload, request mode, timeout, communication options and metadata.
- `AgentTask` carries task ID/status/agent/result/events/cancel.
- `AgentEvent` carries task ID, type, timestamp and object data.
- `ProtocolContext` currently carries tenant ID and arbitrary attributes.
- `ProtocolClient` mirrors send/submit/stream.

Implication: do not create a second generic request/task/event/communicator stack under an ARIP-named module. The strongest potential additions are runtime-specific semantics (identity context, run/session linkage, idempotency, deadline, structured errors, stream cursor/resume, approval lifecycle) if these are not already represented elsewhere.

## 2. Runtime REST surface observed

| Resource | Path | Observed operation | ARIP mapping |
|---|---|---|---|
| `AgentResource` | `/api/v1/agents/run` | Synchronous agent execution request | Run submit/execute; verify response IDs and long-running semantics |
| `AgentResource` | `/api/v1/agents/stream` | SSE stream from agent execution | Run output stream; verify event IDs, replay and disconnect behavior |
| `AgentResource` | `/api/v1/agents/skills` | Skill listing | Partial capability discovery; not necessarily equivalent to the general capability registry |
| `AgentResource` | `/api/v1/agents/skills/{skillId}` | Skill lookup | Capability descriptor lookup only if skill semantics align |
| `ExecutionResource` | `/api/v1/executions/{id}/state` | Execution state read | Run inspect/state |
| `ExecutionResource` | `/api/v1/executions/{id}/transitions` | State transition request | Internal lifecycle control; do not expose arbitrary transitions as generic client operations |
| `ExecutionResource` | `/api/v1/executions/{id}/history` | State history | Run history/diagnostics |
| `ExecutionResource` | `/api/v1/executions/{id}/recovery/*` | Recovery assess/plan/execute | Administrative recovery; not baseline ARIP run semantics |
| `AgentInteractionRestResource` | `/api/v1/agent-interactions/{runId}` | Interaction snapshot | Run interaction inspection |
| `AgentInteractionRestResource` | `/api/v1/agent-interactions/{runId}/events` | Interaction history | Run event history; currently represented as a snapshot response, not an SSE cursor stream |
| `CheckpointResource` | `/api/v1/checkpoints/*` | Save/read/list/verify checkpoints | Runtime recovery primitive; not a general client memory API |
| `SecurityDelegationResource` | `/api/v1/security/*` | Security delegation surface | Inspect before mapping delegated authorization/consent |

### Security observation

`AgentResource.trustedRequest` explicitly discards client-supplied identity-shaped context attributes and binds authenticated principal and tenant from Quarkus `SecurityIdentity`; authenticated calls require a trusted tenant attribute. This is a useful server-side trust-boundary pattern.

Do not infer that all REST resources enforce the same security: inspect each resource's identity, tenant and authorization behavior individually. In particular, state transition and checkpoint resources must be reviewed before exposing them through a product-facing client.

## 3. Runtime gRPC surface observed

### `andalus_api.proto`

- `ProjectService`: project CRUD plus session list/create/remove/rename, scoped to project.
- `CodeService`: `StreamCode` server-streaming and `Generate` unary code generation.
- `AndalusService`: status/readiness, product catalog, profile and SDK boundary queries, command discovery/index/workbench.
- The same proto also declares transcript, SDK/provider/model and knowledge-related messages/services in later sections.

These APIs are useful but not a single unified agent runtime API. `CodeService` is product/domain-shaped and should not become the generic ARIP run service.

### `operator/v1/operator.proto`

The OperatorService declares:
- plugin, sandbox, tool and capability listing/inspection;
- execution listing/get/pause/resume/cancel/retry;
- session listing/get/suspend/resume/close;
- diagnostics and configuration.

Its `OperatorRequestContext` currently carries correlation ID, request ID and string attributes. Confirm authentication, tenant derivation, authorization and scope enforcement in the server implementation before treating it as an external product-client contract.

### gRPC implementation caveat

The inspected `GrpcSessionService` constructs its own `ProjectStore(null)` and reads/writes transcript data directly. This should be reviewed for lifecycle, configuration, tenancy and consistency before treating it as the authoritative session service. The inspected `AndalusGrpcService` exposes platform status/catalog/command queries but is not itself the generic run execution API.

## 4. Existing SDK surface observed

`api/andalus-sdk` has a local Java `AndalusClient` grouping provider, model, inference, project and session APIs. Execution, sandbox and artifact APIs are attached through setters and may be null until configured.

- `AndalusSessionApi` wraps a local `ProjectStore` for project sessions.
- `AndalusExecutionApi` wraps local `ExecutionStateMachine`, `CheckpointStore` and `ExecutionRecoveryCoordinator`.
- This SDK is not yet demonstrated to be a remote typed client for the REST/gRPC runtime server.

Implication: distinguish local embedded SDK APIs from remote product-client APIs. ARIP's first-party remote client cannot simply assume this SDK class is already the remote client facade.

## 5. Initial operation-to-surface matrix

| ARIP semantic | Existing candidate | Current conclusion |
|---|---|---|
| Runtime health/readiness | `AndalusService.GetStatus/GetReadiness`, REST platform endpoints | Reuse; define stable typed response/version |
| Session list/create/close | ProjectService, OperatorService, local SDK | Multiple models; reconcile authority and scope before unifying |
| Run submit | REST `POST /api/v1/agents/run` | Candidate, but verify response shape, durable ID and async lifecycle |
| Run stream | REST `POST /api/v1/agents/stream`, CodeService.StreamCode | SSE exists; gRPC code stream is domain-shaped; no verified generic resumable run stream |
| Run inspect/cancel | OperatorService execution controls, REST state/history, interaction resources | Partial; confirm same execution/run ID namespace and policy |
| Capability discovery | OperatorService ListCapabilities, REST skills | Exists in different scopes; normalize descriptors only after defining authority |
| Approval | Framework approval store/strategy and interaction service | Primitive exists; verify durable externally addressable lifecycle and decision authorization |
| Event resume | Interaction history / event abstractions | No verified cursor/replay contract in inspected resources |
| Idempotency | No confirmed endpoint/client contract in inspected files | Gap to investigate |
| Principal/tenant context | REST agent trusted request; Operator context has correlation/request/attributes | Inconsistent surface; standardize server-derived identity context |
| Context references/batching | No confirmed common operation in inspected files | Defer until measurements show need |

## 6. Priority findings

1. **There is already a substantial foundation.** Generic communication contracts, task/event abstractions, REST agent execution, execution lifecycle, gRPC operator control and capability/approval primitives exist. A new protocol stack would duplicate existing work.
2. **The API surfaces are fragmented.** Project sessions, operator sessions, agent runs, executions, interactions and checkpoints are represented by different models. ARIP needs an explicit mapping/identity relationship, not a premature unified endpoint.
3. **Remote and embedded SDKs are different.** The current Java SDK wraps local runtime objects and is not proven to be the remote ARIP client.
4. **Streaming is the key performance/design gap to validate.** Existing SSE and domain-specific gRPC streaming exist; a generic run event contract needs sequence/cursor, terminal-state and replay guarantees.
5. **Security context needs parity.** The agent REST endpoint has a trusted identity binding pattern; verify the other endpoints and gRPC interceptors before defining client guarantees.
6. **Approval must be first-class.** Existing approval primitives should be mapped to stable run states/events and scoped decisions, not replaced by another approval engine.

## 7. Recommended next engineering sequence

### A0 — Complete source-level contract mapping
Inspect REST resources, gRPC service implementations/interceptors, auth configuration, session and execution stores, approval services, event publishers and tests. Record the authoritative IDs and ownership of session/run/execution/attempt.

### A1 — Define the minimum ARIP contract
Only after A0, specify:
- `RuntimeDescriptor` and feature negotiation;
- `RequestContext` with principal resolution, tenant/resource scope, deadline, correlation and idempotency;
- `RunHandle` and lifecycle state;
- typed `RunEvent` with event ID, sequence and optional resume cursor;
- structured `RuntimeError`;
- approval reference/decision semantics.

Do not include memory APIs, generic context service or broad capability registry unless A0 proves a concrete missing reusable contract.

### A2 — Bind existing APIs
Create adapters over current REST/gRPC/local services; do not change wire APIs just to rename them ARIP. Mark unsupported semantics explicitly.

### A3 — Close verified gaps
Prioritize consistent auth/tenant enforcement, run identity mapping, resumable event stream semantics and idempotent submission. Implement only gaps supported by tests and ADRs.

### A4 — Conformance and performance
Test equivalent behavior across local, REST and gRPC. Benchmark against the current MCP route using the same workload, security and audit settings. Keep MCP for external tool ecosystems.

## 8. Explicit exclusions

- No Kabayan-specific behavior or product naming in ARIP.
- No new `andalus-arip` module in this audit phase.
- No custom wire encoding.
- No duplicate session, execution, event, approval or capability stores.
- No claims of resumability, idempotency, authorization parity or performance until verified by implementation/tests.
