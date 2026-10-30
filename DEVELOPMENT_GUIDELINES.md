# Andalus Framework Development Guidelines

These guidelines describe the intended engineering practices for the reusable framework repository. They complement the root `AGENTS.md`.

## 1. Product and repository boundary

The framework is the reusable foundation for Andalus. It should be usable by more than one runtime and should not encode assumptions about a particular deployment, tenant model, cloud, database, queue, provider or user interface.

The framework should own:
- Domain-neutral agent, invocation, context, tool, workflow and extension contracts.
- Reusable orchestration and execution semantics.
- Provider/tool abstractions and reusable implementations that do not require runtime infrastructure.
- Typed event, state, memory, knowledge, security, policy and observability contracts.
- Extension points, schemas and conformance tests.

The runtime repository should own concrete deployment wiring, infrastructure adapters, persistence, transport endpoints and operational services.

## 2. Module boundaries

Each module must have a single primary responsibility and a documented public surface.

Every module README should state:
- What the module owns and explicitly does not own.
- Which packages are public API and which are internal.
- Allowed dependency direction.
- Extension points and default implementations.
- Thread-safety, lifecycle and error semantics.
- Test and compatibility expectations.

Rules:
- Keep the dependency graph acyclic.
- Keep foundational modules small and stable.
- Separate API/SPI from default implementation when doing so enables alternative implementations or avoids unwanted dependencies.
- Avoid module proliferation and avoid oversized catch-all modules.
- Do not expose internal package types from public APIs.
- Do not introduce a new module for a concept already owned by a sibling module without an explicit consolidation decision.

## 3. Contracts and API design

- Prefer interfaces and immutable value types for cross-module contracts.
- Make nullability, optionality, ownership, mutability and thread-safety explicit.
- Define cancellation, timeout, error, retry and resource-close semantics.
- Use typed identifiers and structured errors where they materially improve safety and interoperability.
- Keep public contracts independent of vendor SDKs and runtime frameworks.
- Avoid leaking implementation classes, persistence entities or transport DTOs into contracts.
- Use explicit versioning for serialized schemas and externally consumed events.

Before adding a contract, search for existing concepts in core, SPI, execution, state, event, workflow, agent, context and runtime SPI modules. If overlap exists, choose one canonical owner and document adapters or migration steps.

## 4. Execution, state and lifecycle semantics

Execution contracts are foundational and must be consistent across the ecosystem.

- Define a single canonical lifecycle model and legal transition rules.
- Keep execution identity and metadata consistent across invocation, event, state and checkpoint contracts.
- Separate execution semantics from storage implementation.
- Specify whether state transitions are synchronous, durable, replayable and observable.
- Define cancellation and timeout behavior for every long-running operation.
- Make checkpoint schema evolution and compatibility explicit.
- Define idempotency and side-effect semantics for replay and retry.
- Treat human approval and other external obligations as explicit suspended/waiting states.
- Keep runtime-specific persistence interfaces/adapters outside the framework unless they are truly portable contracts.

## 5. Extensibility and plugin design

- Define extension points with explicit interfaces, metadata and lifecycle.
- Document discovery, registration, ordering, version compatibility and failure behavior.
- Validate extension metadata and configuration at startup or registration time.
- Avoid hidden global mutable registries.
- Provide safe defaults and clear behavior when an optional extension is absent.
- Ensure extensions cannot bypass security or policy checks through alternate invocation paths.

## 6. Agent, tool and workflow design

- Keep reasoning strategies (for example ReAct, planning, reflection and research) composable rather than hard-wired to one provider.
- Separate agent strategy from model selection, context assembly, memory, tool execution and policy enforcement.
- Define workflow semantics independently from any one scheduler or runtime worker.
- Treat tool schemas as contracts; validate input and output and define timeout/cancellation behavior.
- Make side effects explicit and expose idempotency requirements to callers.
- Keep human-in-the-loop behavior as a portable contract with runtime-owned delivery mechanisms.

## 7. Security and trust boundaries

- Treat prompts, retrieved content, tool results and extension metadata as untrusted.
- Define portable authorization and policy contracts without embedding one identity provider.
- Never place secrets in framework defaults, logs, examples or test fixtures.
- Make security context propagation explicit and immutable where practical.
- Ensure policy enforcement is not optional merely because a caller uses a lower-level API.
- Document which guarantees are provided by the framework and which require runtime enforcement.

## 8. Observability and resilience

- Provide portable hooks and semantic conventions; keep vendor exporters in adapter modules.
- Use structured, bounded-cardinality telemetry.
- Define consistent error categories and retryability.
- Ensure cancellation and deadlines propagate through nested operations.
- Avoid implicit retries for operations with external side effects.
- Keep resilience policies configurable and composable rather than hard-coded into agent strategies.

## 9. Testing strategy

- Unit-test value types, state machines, validation, orchestration logic and policy decisions.
- Add contract tests for public SPIs and conformance suites for implementations.
- Test serialization round trips and schema-version compatibility.
- Test cancellation, timeout, concurrency, invalid transitions, extension failure and resource cleanup.
- Keep tests deterministic and independent of live model providers or infrastructure.
- Add integration tests only where module wiring or external compatibility needs validation.

## 10. Build, dependencies and releases

- Use the Maven Wrapper and the Java version declared by the parent POM.
- Keep dependency versions centralized and minimize transitive dependencies.
- Keep contract modules lightweight and free from runtime-specific frameworks.
- Verify downstream runtime compatibility for public API changes.
- Align framework and runtime artifact versions through an explicit compatibility policy.
- Publish immutable artifacts and document compatibility between releases.
- Do not change artifact coordinates or publishing destinations without a cross-repository release plan.

## 11. Documentation and pull requests

Every public module needs practical documentation and compiling examples where feasible. Public API changes must include compatibility notes, migration guidance and tests.

A pull request should explain:
- The problem and intended contract.
- Why the capability belongs in this module.
- Alternatives considered and dependency impact.
- Compatibility and serialization impact.
- Tests actually run and remaining limitations.

## 12. Current architectural focus

The first cross-repository priority is a source-of-truth map for execution context, lifecycle, checkpoint/state, event and orchestration contracts. Identify duplicates and consumers before moving or deleting types. Introduce a canonical model with adapters and conformance tests, then migrate runtime modules incrementally.
