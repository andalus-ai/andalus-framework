# ARIP — Andalus Runtime Interaction Profile

Status: M0 proposal; not yet a frozen public protocol
Owners: Andalus Framework (portable contracts), Andalus Runtime (dispatch and transport bindings), client projects (including Kabayan)

## 1. Purpose

ARIP defines a typed, efficient interaction profile for product runtimes that need to invoke and manage work on Andalus Runtime. Kabayan is the first motivating client, but ARIP must remain product-neutral.

ARIP is an interaction profile over existing Andalus communication and runtime foundations. It is **not** a new network stack, wire protocol, agent discovery system, or replacement for MCP, A2A or ANP.

## 2. Reuse before extension

The Framework already provides protocol-neutral communication contracts in `andalus-communication-api`, local in-process communication in `andalus-protocol-local`, and separate A2A/ANP adapters. The Runtime already exposes REST/gRPC and run lifecycle APIs.

Therefore:
- Extend existing contracts only where a concrete semantic gap exists.
- Prefer mapping ARIP operations to existing runtime APIs.
- Do not add an `andalus-arip` module in M0.
- Do not create a second session, run, capability registry, approval or event model.
- Keep framework contracts free of Quarkus, persistence, network-server and vendor dependencies.

## 3. Logical operation groups

These are logical client operations. They may be multiplexed, batched, streamed or mapped to existing endpoints; they do not imply one HTTP/gRPC request per operation.

| Group | Logical operations | Purpose |
|---|---|---|
| Session | `session.open`, `session.get`, `session.close` | Establish caller and conversation context |
| Agent | `agent.invoke`, `agent.stream` | Invoke an agent and receive incremental output |
| Run | `run.submit`, `run.observe`, `run.cancel` | Manage long-running execution |
| Capability | `capability.discover`, `capability.invoke` | Discover and invoke policy-authorized capabilities |
| Context | `context.resolve` | Resolve scoped context references without over-fetching |
| Approval | `approval.request`, `approval.respond` | Human-in-the-loop decision lifecycle |
| Memory | `memory.query`, `memory.mutate` | Optional delegated access to runtime-managed memory |

Memory operations are optional and must not make Kabayan's personal-memory semantics a Framework concern. Kabayan may own the personal memory policy while using Andalus storage/retrieval primitives.

## 4. Transport and deployment bindings

### In-process

Use direct typed Java interfaces and asynchronous return types when Kabayan and Andalus share a JVM. Preserve the same identity, authorization, policy, cancellation, tracing and audit semantics as remote calls. In-process is an optimization, not a trusted bypass.

### Remote

Prefer the existing gRPC APIs for typed request/response and bidirectional streaming between separate processes. Reuse current protobuf contracts where they already express the required semantics. Add or change protobuf messages only after endpoint mapping and compatibility review.

### HTTP

Reuse existing REST APIs for simple clients, administration, diagnostics and interoperability. Do not create an ARIP-only HTTP server if the runtime's current API can serve the need.

### External tools and agent interoperability

- MCP remains the preferred integration surface for third-party tool servers and broad tool interoperability.
- A2A remains for interoperable agent task exchange.
- ANP remains for agent-network discovery/communication semantics already owned by its modules.
- ARIP is for a trusted product client to interact efficiently with its configured Andalus Runtime.

## 5. Common request context

Every operation that can access or mutate data must carry or resolve the following context:

- authenticated principal and tenant/owner scope;
- session and, where applicable, run identifiers;
- requested capability and resource scope;
- correlation/trace identifier and idempotency key for retryable mutations;
- deadline and cancellation signal;
- policy/consent context;
- schema/API version.

The server derives authoritative identity from authentication. Client-supplied tenant or user identifiers are selectors to validate, never proof of identity.

## 6. Common response and failure semantics

Responses should be typed and versioned, with stable machine-readable error codes. Distinguish:
- invalid request/schema;
- unauthenticated and unauthorized;
- policy denied or approval required;
- resource not found or scope mismatch;
- conflict or idempotency replay;
- rate limited;
- deadline exceeded or cancelled;
- transient infrastructure failure;
- unsupported operation/version.

Do not expose internal stack traces or secrets. Retry only when the operation is idempotent or protected by an idempotency key.

Streaming must define terminal states, ordering, resumability (if supported), backpressure, cancellation and partial-result semantics. A disconnected client must not silently imply that a consequential action was approved or that a run should be abandoned.

## 7. Security invariants

1. Authenticate every remote connection; authorize every operation.
2. Enforce tenant and resource scope on the server, including in-process calls.
3. Evaluate capability policy before execution, not only at discovery time.
4. Keep approval as a first-class state transition; approval tokens are scoped, expiring and single-purpose.
5. Emit audit events for invocation, denial, approval, mutation and cancellation.
6. Apply rate, quota, deadline and payload-size limits.
7. Treat retrieved context, tool output and client metadata as untrusted data.
8. Never weaken these controls to reduce protocol overhead.

## 8. Efficiency model

The performance objective is to remove unnecessary layers, not to invent a faster wire format.

- Avoid routing a first-party Java client through MCP schema discovery and tool-call wrapping when a typed runtime API is available.
- Reuse a long-lived gRPC channel and bidirectional streams for remote run observation.
- Batch related reads and capability discovery where semantics allow.
- Support context references and bounded projections instead of repeatedly serializing large context objects.
- Use in-process calls for same-JVM deployments.
- Measure latency, allocations, payload size, throughput and cancellation behavior before introducing custom codecs or binary framing.

No performance claim is accepted without a benchmark against the existing REST/gRPC and MCP paths under equivalent security and audit settings.

## 9. Compatibility and evolution

- Version public schemas and protobuf contracts explicitly.
- Prefer additive fields and operations; define unknown-field behavior.
- Do not reuse field numbers or silently change enum meanings.
- Publish compatibility tests for local and remote bindings.
- Keep framework API dependencies minimal and runtime-independent.
- Document deprecation windows and client migration paths.

## 10. Initial implementation sequence

1. Inventory existing Runtime REST/gRPC endpoints and Framework communication contracts.
2. Produce an operation-to-endpoint mapping; mark true gaps rather than assuming new APIs are needed.
3. Define a small typed Java client facade over current endpoints.
4. Define common request context, error mapping, streaming and cancellation behavior.
5. Add in-process binding only where the runtime exposes a safe service boundary.
6. Add contract/conformance tests and benchmark against MCP for representative first-party workflows.
7. Only then decide whether any new Framework SPI or protobuf contract is justified.

## 11. Open decisions before freezing ARIP

- Which existing runtime API owns session lifecycle versus conversation lifecycle?
- Are run observation streams resumable, and what cursor/event retention guarantees exist?
- What idempotency semantics are already implemented for submissions and mutations?
- Which approval API and token scope are authoritative?
- Is context resolution already represented by the current context API?
- Which memory operations should be exposed to external product clients, if any?

These must be answered from the current Andalus implementation before code-level contracts are introduced.
