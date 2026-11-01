# Phase 1 Dependency Architecture Audit

- **Status:** Initial static audit; proposed remediation, not an approved API-breaking change
- **Date:** 2026-11-01
- **Scope:** Andalus Framework and its documented boundary with Andalus Runtime
- **Related:** [Contract inventory](CONTRACT_INVENTORY.md), [ADR-0001](ADR-0001-execution-context-ownership.md), [ADR-0002](ADR-0002-checkpoint-contract-ownership.md)

## Executive summary

The Framework is not yet a consistently framework-only dependency layer. Several modules directly depend on Quarkus APIs, one observability module depends on the engine module, and runtime-specific SPIs are packaged inside the Framework repository. Separately, the existing contract inventory identifies duplicate public fully qualified names for execution context and checkpoint storage.

The immediate recommendation is **not** to remove Quarkus or rename public APIs in this pass. First separate portable contracts from integration implementations, map every consumer, and establish enforceable dependency direction.

## Confirmed findings

| ID | Severity | Finding | Evidence / impact |
|---|---|---|---|
| DEP-01 | High | Duplicate public FQN `tech.kayys.andalus.execution.ExecutionContext` is declared in `andalus-engine` and `andalus-execution-api`. | Two unrelated scopes (graph/node state versus invocation metadata/security/lifecycle) are published under one FQN. A downstream classpath containing both artifacts is ambiguous. See ADR-0001. |
| DEP-02 | High | Duplicate public FQN `tech.kayys.andalus.execution.CheckpointStore` is declared in `andalus-engine` and `andalus-runtime-spi`. | The contracts persist different payloads (`ExecutionSnapshot` versus `AgentContext`) and have incompatible method shapes. See ADR-0002. |
| DEP-03 | High | `andalus-observability` depends on `andalus-engine`. | Observability is a cross-cutting capability and should not need the orchestration/engine implementation layer. This creates an avoidable dependency direction and complicates reuse. |
| DEP-04 | Medium | Quarkus APIs are direct dependencies of `andalus-graph`, `andalus-plugin`, `andalus-observability`, and `andalus-hitl`. | These modules cannot be treated as plain-Java portable modules as currently packaged. Separate portable contracts/core from Quarkus integration adapters where practical. |
| DEP-05 | Medium | `andalus-runtime-spi` is built as a Framework module while its own description identifies it as an SPI for Andalus Runtime. | Runtime lifecycle and persistence ports should have an explicit owner. Decide whether the contracts are genuinely cross-runtime extension APIs or belong in the Runtime repository. |
| DEP-06 | Medium | Java compiler `source`/`target` properties are repeated across many child POMs. | This undermines the parent-level compiler policy and makes toolchain changes inconsistent. Use inherited `maven.compiler.release` unless a module has a documented exception. |
| DEP-07 | Low | Quarkus BOM is imported in both the Framework root POM and nested `framework/pom.xml`. | The child import is redundant unless it intentionally overrides dependency management; keep one authoritative import and document any exception. |

## Dependency evidence

### Quarkus-coupled Framework modules

Static POM/source search found direct Quarkus coupling in:

- `andalus-graph`: `quarkus-arc`.
- `andalus-plugin`: `quarkus-core`.
- `andalus-observability`: `quarkus-arc`; source includes Quarkus-specific `@DefaultBean`.
- `andalus-hitl`: Quarkus REST, Hibernate Reactive/Panache, Mailer, Scheduler, Vert.x and related test/runtime extensions.

This does not mean Quarkus is inherently wrong. It means the modules should be classified explicitly as portable API/core or Quarkus integration modules, rather than presenting all Framework artifacts as equally runtime-neutral.

### Observability dependency direction

The `andalus-observability` POM directly depends on:

- `andalus-extension`
- `andalus-configuration`
- `andalus-resilience`
- `andalus-core`
- `andalus-engine`

The engine dependency is the architectural concern. Prefer narrow telemetry/event/context contracts in lower-level modules; the engine should depend on observability ports, not the reverse. Confirm actual source usage before changing the POM.

### Execution and checkpoint contracts

The existing contract inventory and ADRs already identify the duplicate FQNs and proposed ownership. Search results also show graph-oriented consumers in `andalus-engine` (including `ExecutionEngine`, `ExecutionNode`, `EdgeCondition`, planner/reasoner and execution service), while invocation-level consumers include `AgentInvocation` and `DefaultExecutionContext`.

This is not yet a complete reference map. The checkpoint interfaces' full implementation, injection and runtime read/write paths still need to be enumerated before any migration.

## Target dependency rules

1. **API before implementation:** contract modules may depend only on other stable, lower-level contracts and value types.
2. **No upward dependency:** observability, security, state and execution API modules must not depend on agent orchestration or runtime implementations.
3. **Adapters at the edge:** Quarkus/CDI, persistence engines, HTTP servers and vendor SDKs belong in explicitly named integration/adapter modules.
4. **Runtime ownership is explicit:** Runtime-specific lifecycle, scheduling, persistence and execution SPI contracts must either move to Runtime or be justified as stable cross-runtime extension contracts.
5. **No duplicate public FQNs:** add a build-time classpath check across published reactor artifacts.
6. **One compiler policy:** parent POM owns `maven.compiler.release`; module overrides require a documented reason.
7. **No destructive API cleanup without migration:** preserve existing public contracts until consumer maps, adapters, compatibility requirements and tests are complete.

## Recommended remediation sequence

### A. Complete the evidence map
- Enumerate every duplicate FQN declaration, import, implementation, method signature, CDI injection point and service registration across both repositories.
- Map every checkpoint writer and recovery reader, including payload serializers and persisted schema versions.
- Inspect source usage of `andalus-engine` from `andalus-observability` and identify the minimum contracts actually required.
- Record all Quarkus imports and annotations per module, not only POM dependencies.

### B. Establish module classifications
- Mark each module as `api`, `core`, `adapter-quarkus`, `adapter-persistence`, or `runtime`.
- Split Quarkus-specific implementations from portable contracts only where there is a real non-Quarkus use case.
- Decide ownership of `andalus-runtime-spi` with Runtime maintainers before moving artifacts.

### C. Enforce boundaries
- Add Maven Enforcer rules for forbidden dependency edges between module groups.
- Add duplicate-class detection for the complete runtime dependency graph.
- Add API conformance tests for invocation context, graph context and checkpoint ports.
- Add a dependency architecture document/table that is reviewed when modules are added.

### D. Migrate incrementally
- Approve ADRs for canonical context and checkpoint ownership.
- Introduce distinct graph-context and agent-context snapshot port names.
- Add adapters and compatibility tests; migrate Runtime consumers.
- Deprecate old contracts for a planned compatibility window; remove only in a breaking release.

## Acceptance criteria

- [ ] No duplicate public FQNs in the resolved Framework + Runtime classpath.
- [ ] Invocation and graph execution contexts have distinct, documented responsibilities.
- [ ] Checkpoint ports have distinct names and documented payload/recovery guarantees.
- [ ] Observability has no dependency on engine implementation.
- [ ] Every Quarkus-coupled module is explicitly classified as an adapter/integration module or has a documented reason to remain coupled.
- [ ] Runtime-specific SPI ownership is decided and documented.
- [ ] Dependency rules and duplicate-class checks run in CI.
- [ ] Both repository reactors build and relevant integration tests pass from a clean checkout.

## Validation limitations

This is a static audit based on POM and source search results. It does not prove a clean Maven build, complete transitive dependency graph, all source references, runtime recovery behavior, binary compatibility, or the exact packaging failure mode. No public Java API was changed as part of this audit.