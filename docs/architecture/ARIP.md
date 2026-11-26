# ARIP — Andalus Runtime Interaction Profile

**Status:** Architecture proposal; contracts are not frozen  
**Scope:** Neutral first-party product-to-runtime interaction  
**Owner:** Andalus Framework (portable contracts); Andalus Runtime (bindings and enforcement)

## 1. Purpose and positioning

ARIP is a typed interaction profile for applications that invoke and manage work on an Andalus Runtime. It standardizes the *meaning* of operations, request context, lifecycle events, errors, cancellation, and compatibility across local and remote deployments.

ARIP is not a new transport, wire format, agent-discovery protocol, or replacement for MCP, A2A, or ANP. It must be implemented by reusing existing Andalus communication abstractions and Runtime APIs wherever possible.

## 2. Layering

- **Framework contracts:** transport-neutral Java interfaces/value types and SPI contracts. No Quarkus, gRPC, HTTP server, persistence implementation, or product-specific dependency.
- **Runtime bindings:** maps ARIP operations to authoritative session, run, capability, policy, approval, event, and audit services.
- **Transports:** local/in-process, gRPC, and REST bindings. They expose the same semantics and security behavior.
- **Product clients:** Kabayan, Cifr, Farabi and other products consume typed client APIs; none owns ARIP semantics.

ARIP must not be implemented as a second copy of `AgentCommunicator`, `AgentTask`, `AgentRequest`, or the protocol SPI. First evaluate whether those existing abstractions can be extended compatibly or composed behind a narrower runtime-client facade.

## 3. Existing framework foundation

The Framework currently contains `andalus-communication-api` with:
- `AgentCommunicator`: one-shot send, submit and event streaming;
- `AgentRequest`: target, capability, payload, mode, timeout, communication options and metadata;
- `AgentTask`: task identity/status/result/events/cancellation;
- `AgentEvent`: typed task event with timestamp and data;
- `ProtocolContext`: tenant identifier and attributes;
- `ProtocolClient` and protocol identity/version abstractions.

The Framework also contains distinct agent-network and A2A/ANP abstractions. ARIP must remain an application-to-configured-runtime interaction profile, not be conflated with agent-to-agent networking.

### Initial contract gaps to evaluate

The existing communication types are generic and useful, but the inspected API surface does not yet demonstrate a complete portable contract for:
- authenticated principal and authorization context (tenant alone is insufficient);
- explicit session and run identity/lifecycle;
- idempotency and deadline/cancellation propagation;
- resumable ordered event streams with cursors;
- structured, stable error taxonomy;
- approval request/decision lifecycle;
- capability discovery with policy-aware invocation;
- version negotiation and conformance guarantees.

These are candidate gaps, not a mandate to add all of them. Confirm each against the full Framework and Runtime before introducing types.

## 4. Logical operation model

The following groups define semantic capabilities, not fixed RPC names or mandatory endpoints.

| Group | Required semantics | Initial disposition |
|---|---|---|
| Runtime info | identity, version, health/readiness, supported ARIP features | Map to existing status/readiness surfaces; avoid a duplicate catalog |
| Session | open/get/close and bind caller context | Existing project sessions and operator sessions are not yet shown to be one unified conversation/session contract |
| Run | submit, inspect, await, cancel, retry where supported | Existing agent run and execution lifecycle APIs are separate surfaces; map carefully |
| Events | ordered stream, terminal state, cancellation, optional resume cursor | REST SSE and task/event abstractions exist; verify durability/order/resume |
| Capability | discover descriptors and invoke an authorized capability | Existing registries and operator catalog exist; distinguish discovery from authorization |
| Approval | request, inspect, approve/reject/expire | Interaction/approval primitives exist; verify externally addressable lifecycle and authorization |
| Context | resolve bounded references/projections | Do not add until a concrete repeated-context payload problem is measured |
| Memory | optional query/mutation against runtime-managed primitives | Not required for core ARIP; personal-memory semantics remain product-owned |

## 5. Canonical request context

A request that reads or mutates runtime state must resolve:

- authenticated principal (server-derived);
- tenant and resource scope (server-validated);
- optional session, run and parent/correlation identifiers;
- operation and capability scope;
- deadline and cancellation;
- idempotency key for retryable mutations;
- policy/consent/approval context;
- API version and negotiated feature set.

Never trust client-supplied principal or tenant fields as proof of identity. The transport authenticates the caller; Runtime derives principal and authoritatively binds tenant/resource scope. A local binding must pass through equivalent authorization and audit checks.

## 6. Run and event semantics

A run has a stable identifier and an explicit state machine. At minimum, clients must be able to distinguish accepted, running, waiting-for-approval, completed, failed, cancelled and expired/unknown states where the Runtime supports them.

Events must define:
- per-run sequence/cursor and stable event identifier;
- event type, timestamp and typed payload/version;
- replay/resume behavior and retention window (or explicitly declare streams non-resumable);
- terminal event semantics;
- backpressure and client cancellation behavior;
- whether disconnecting a subscriber affects execution (normally it must not).

Do not equate an SSE connection or a gRPC stream with the run itself. A run can outlive its observer.

## 7. Error model

Expose stable machine-readable categories: invalid request, unauthenticated, unauthorized, policy denied, approval required, not found/scope mismatch, conflict, idempotency conflict/replay, rate limited, deadline exceeded, cancelled, unsupported feature/version, and transient internal/unavailable.

Map transport status codes to this common semantic model. Do not leak stack traces, secrets, or implementation internals. Retries require idempotency or an operation explicitly documented as safe to retry.

## 8. Transport bindings

### In-process

Use typed Java interfaces and asynchronous primitives. Avoid serialization and network hops, but preserve authentication principal, tenant scope, policy checks, audit, deadlines and cancellation. In-process is not a privileged bypass.

### gRPC

Preferred for cross-process first-party clients requiring typed contracts and bidirectional streaming. Reuse existing protobuf services/messages where their semantics match. Introduce versioned protobuf additions only for verified gaps. Use long-lived channels and streams; avoid per-token/per-event unary RPCs.

### REST

Keep REST for compatibility, administrative operations, simple integrations and diagnostics. Reuse existing resources. SSE may serve event observation where replay and cursor semantics are explicitly documented.

### MCP / A2A / ANP

- MCP: external tool-server interoperability and tool ecosystems.
- A2A: agent-to-agent task exchange.
- ANP: agent-network discovery/communication semantics.
- ARIP: product client to its configured Andalus Runtime.

Do not tunnel ARIP through MCP internally just to invoke first-party Runtime capabilities.

## 9. Efficiency requirements

Optimize unnecessary layers and round trips, not security or semantics:
1. typed in-process client when co-located;
2. long-lived gRPC channel and bidirectional event stream remotely;
3. batch related reads/discovery;
4. bounded context projections/references instead of repeated large payloads;
5. explicit cancellation/deadlines and bounded queues;
6. measure latency, allocations, payload size and throughput against current REST/gRPC/MCP paths under equivalent authorization and audit.

No claim that ARIP is faster than MCP is valid without representative benchmarks.

## 10. Compatibility and conformance

- Version public Java contracts and protobuf APIs independently but map them explicitly.
- Prefer additive changes; never reuse protobuf field numbers or silently change enum meanings.
- Publish contract fixtures and conformance tests for local and remote bindings.
- Define unknown-field/unknown-feature behavior and minimum supported versions.
- Maintain parity tests proving equivalent authorization, policy, audit, cancellation and terminal outcomes across transports.
- Keep framework API dependencies portable and minimal.

## 11. Implementation gates

1. Complete the Runtime endpoint/service inventory and operation mapping.
2. Confirm the actual session/run/approval/event ownership models.
3. Decide which existing communication contracts are reusable as-is, composable, or require additive extension.
4. Specify principal, tenant, idempotency, deadline and event-cursor semantics.
5. Implement a small typed client facade over existing services before adding a new module.
6. Add conformance tests and benchmarks.
7. Only create a dedicated ARIP module if reuse/composition is insufficient and a stable public API boundary has emerged.

## 12. Non-goals

- No second agent runtime, session store, run state machine, approval engine, capability registry, event bus or transport stack.
- No product-specific memory, identity, UI, automation policy or personal assistant semantics.
- No custom binary framing or bespoke serialization in the absence of benchmark evidence.
- No new mandatory MCP hop between first-party products and Andalus Runtime.
