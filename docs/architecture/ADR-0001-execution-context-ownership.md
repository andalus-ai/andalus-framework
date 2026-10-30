# ADR-0001: Separate Invocation and Graph Execution Contexts

- **Status:** Proposed
- **Date:** 2026-10-30
- **Decision owners:** Framework and runtime maintainers
- **Related documents:** [Contract inventory](CONTRACT_INVENTORY.md), [Contract consumer map](CONTRACT_CONSUMER_MAP.md)

## Context

Two framework Maven artifacts currently publish an interface with the same fully qualified name:

| Artifact | Type | Responsibility |
|---|---|---|
| `andalus-execution-api` | `tech.kayys.andalus.execution.ExecutionContext` | Agent invocation metadata, cancellation, security snapshot and lifecycle |
| `andalus-engine` | `tech.kayys.andalus.execution.ExecutionContext` | Graph/node execution state: graph, variables, resources, current node, parent and child context |

These are not two views of one interchangeable abstraction. They represent different scopes and expose materially different operations. The runtime's dependency graph includes both artifacts, so downstream consumers can encounter an ambiguous binary type when both artifacts are present. The exact compiler or class-loader failure mode depends on the resolved dependency graph and packaging; it has not yet been established by a clean build.

The engine's public `ExecutionEngine` methods accept the graph-oriented type. The invocation API's implementation is provided separately by `andalus-execution-core`. Renaming the graph contract therefore changes public engine method signatures and may affect implementors, adapters, tests and downstream consumers.

## Decision proposed

1. Reserve `tech.kayys.andalus.execution.ExecutionContext` for invocation-level context, owned by `andalus-execution-api`.
2. Rename the graph/node contract to `tech.kayys.andalus.execution.GraphExecutionContext` (or another explicitly approved graph-scoped name) in the engine API.
3. Keep graph-specific capabilities—graph, variables, resources, current node, parent graph context and child graph context—on the graph-scoped contract. Do not merge these methods into the invocation-level contract.
4. Define an explicit composition/propagation policy between the two scopes:
   - invocation identity and metadata are available to graph execution;
   - cancellation and security context propagate to child graph contexts;
   - graph variables and current-node state remain graph-scoped;
   - child contexts retain a traceable parent relationship.
5. Do not introduce a deprecated `ExecutionContext` alias in the engine's existing package: that would preserve the duplicate fully qualified name and fail to resolve the underlying ownership problem.
6. Migrate all engine and runtime implementations, call sites, tests, fixtures, examples and extension points in one coordinated compatibility change. If external binary compatibility is required, publish a migration/major-version plan rather than silently changing the method descriptors.

## Alternatives considered

### Merge both interfaces

Rejected. Invocation lifecycle/security/cancellation and graph/node state have different lifetimes and ownership. A merged interface would force unrelated consumers to depend on capabilities they do not need and would make linear/non-graph execution semantics unclear.

### Keep both names and rely on module boundaries

Rejected as a long-term design. The runtime already consumes both artifacts, and downstream applications may do the same. Source-level import disambiguation does not make two different binary definitions with the same FQN safe on one classpath.

### Add a deprecated alias

Rejected. A same-package alias named `ExecutionContext` in the engine artifact would continue publishing the colliding FQN. A compatibility adapter must use a distinct type name/package.

## Required migration work

The following must be completed before the source rename is merged:

- [ ] Produce a complete repository-wide reference map for both FQNs, including imports, fully qualified references, method signatures and generic/nested types.
- [ ] Identify every implementation of the graph context and every implementation of `ExecutionEngine`.
- [ ] Inspect runtime repository consumers, CDI wiring, service-loader registrations, extension APIs and examples.
- [ ] Decide whether `ExecutionEngine` should accept both invocation and graph context, or accept a graph context that explicitly exposes its invocation context.
- [ ] Define behavior for linear execution; do not represent unsupported graph state using unexplained `null` values.
- [ ] Add contract tests for child-context inheritance, cancellation/security propagation, parent identity and graph-state isolation.
- [ ] Add a dependency-level check that fails when two resolved artifacts contain the same public class FQN.
- [ ] Run clean framework and runtime Maven builds, relevant module tests and packaging/classpath verification.
- [ ] Document downstream source and binary migration requirements.

## Consequences

### Positive

- Each context has one clear responsibility and owner.
- Graph execution remains reusable without forcing graph concepts into invocation-level APIs.
- Runtime and extensions can depend on the narrowest appropriate contract.
- The design makes linear execution versus graph execution explicit.

### Costs and risks

- Public method signatures and implementations need coordinated migration.
- Some adapters may need to carry both invocation and graph-scoped context.
- The migration may require a major version or a compatibility window.
- The final type name and composition boundary still require maintainer approval.

## Current implementation status

This ADR records the proposed ownership and migration constraints only. No public type has been renamed by this change. The current source map is incomplete, and no clean Maven build or classpath verification has been run. The rename must remain blocked until the required reference map and compatibility plan are complete.
