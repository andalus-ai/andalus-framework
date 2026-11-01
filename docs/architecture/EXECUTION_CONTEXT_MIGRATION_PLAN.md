# ExecutionContext Migration Plan

Status: source-reference inventory and migration plan; no public API changes in this change.
Date: 2026-11-01
Related: [ADR-0001](ADR-0001-execution-context-ownership.md), [Contract consumer map](CONTRACT_CONSUMER_MAP.md)

## 1. Decision to implement

Adopt the ownership proposed by ADR-0001:

- `tech.kayys.andalus.execution.ExecutionContext` in `andalus-execution-api` remains the invocation-scoped contract (metadata, cancellation, security snapshot and lifecycle).
- The graph/node contract in `andalus-engine` is renamed to `GraphExecutionContext`.
- Graph-specific state remains graph-scoped. Do not merge graph methods into the invocation API.
- Runtime code must explicitly represent linear/request execution rather than returning null for graph-only capabilities.

This document records a proposed migration plan. It does not itself approve a breaking release or change source APIs.

## 2. Confirmed Framework references

A default-branch code search for the exact import `tech.kayys.andalus.execution.ExecutionContext` returned these source consumers:

| Source | Current meaning | Migration |
|---|---|---|
| `framework/andalus-engine/.../planner/Planner.java` | Graph context in planner SPI | Change parameter to `GraphExecutionContext` |
| `framework/andalus-engine/.../reasoner/Reasoner.java` | Graph context in reasoner SPI | Change parameter to `GraphExecutionContext` |
| `framework/andalus-engine/.../service/ExecutionService.java` | Engine execution context in service API | Review method semantics; use graph context only where graph operations are required |
| `framework/andalus-observability/.../tracing/TracingHelper.java` | Tracing helper context | Depend on a narrow tracing/identity view or invocation context; avoid engine dependency |
| `framework/andalus-knowledge/.../knowledge/KnowledgeProvider.java` | Knowledge provider context | Depend on a narrow knowledge request/context contract; avoid engine dependency |
| `framework/andalus-execution-core/.../DefaultAgentInvocation.java` | Invocation context | Keep invocation `ExecutionContext` |
| `framework/andalus-execution-core/.../DefaultExecutionContext.java` | Invocation context implementation | Keep invocation `ExecutionContext` |

The following graph API consumers are confirmed by source signatures in `andalus-engine`:

- `ExecutionEngine` agent/workflow/skill methods.
- `ExecutionNode.execute`.
- `EdgeCondition.evaluate`, including the built-in always/success/failure/expression/probability conditions.
- Graph context parent/child operations are part of the graph contract itself.

This search is a concrete source-reference inventory, not a complete symbol index. Fully-qualified references, reflection, generated sources, service loading, downstream consumers and test-only references still require a checkout-wide scan.

## 3. Dependency boundary implications

- `andalus-engine` currently owns the graph context and its `ExecutionEngine` API.
- `andalus-execution-api` owns invocation context and is implemented by `andalus-execution-core`.
- `andalus-observability` directly depends on `andalus-engine`; the tracing helper should not force consumers to bring in graph execution.
- `andalus-knowledge` directly depends on `andalus-engine`; knowledge provider APIs should use a narrow knowledge-specific context or a minimal shared execution metadata contract.
- Runtime-core has been recorded in the contract consumer map as depending on both `andalus-execution-api` and `andalus-engine`. Its `DefaultExecutionEngine` and `SimpleExecutionContext` are key coordinated migration points.

The duplicate FQN is an artifact-level collision risk. A source import cannot express which artifact's class is intended when both definitions enter one compile classpath. The exact observed compiler/runtime failure must be established with a resolved dependency tree and clean build; this plan does not claim a specific failure has already been reproduced.

## 4. Migration sequence

### Step A — Complete inventory (required before source edits)

1. Checkout both Framework and Runtime at the exact revisions selected for the migration.
2. Search all source, test, generated-source configuration, examples and docs for:
   - fully qualified and imported `tech.kayys.andalus.execution.ExecutionContext`;
   - unqualified `ExecutionContext` declarations/usages in affected packages;
   - implementations of graph context and `ExecutionEngine`;
   - reflection, CDI producers, service-loader files and serialized type names.
3. Record every implementation and call site, including downstream modules and test fixtures.
4. Capture the resolved runtime-core dependency tree and determine which artifact currently wins classpath ordering.

### Step B — Introduce the graph-specific contract

1. Add `GraphExecutionContext` in the engine module with the graph contract's current methods.
2. Update all engine interfaces, implementations and tests to use the new name.
3. Do not leave an engine `ExecutionContext` type at the old FQN; that would preserve the collision.
4. Add contract tests for child context inheritance, parent identity, variable isolation and graph/resource behavior.
5. Add an explicit invocation-context association only if the engine needs it; define cancellation/security propagation rather than silently duplicating those fields.

### Step C — Migrate Runtime and dependent Framework modules

1. Update Runtime `DefaultExecutionEngine`, its implementation/factory and tests to accept `GraphExecutionContext`.
2. Replace `SimpleExecutionContext`'s null graph/current-node behavior with an explicit linear execution representation or a well-defined optional graph capability.
3. Narrow `TracingHelper` and `KnowledgeProvider` so neither requires the engine artifact solely for context metadata.
4. Update examples, docs and public extension points.

### Step D — Compatibility and release

1. Since the old graph and invocation contracts share one FQN, a same-package deprecated alias cannot preserve both contracts on a combined classpath.
2. Treat the signature rename as a coordinated breaking API change unless maintainers choose a separately versioned migration artifact/package and prove it avoids the collision.
3. Publish migration notes and update all in-organization consumers in the same release train.

## 5. Required validation gates

- [ ] Complete cross-repository reference/implementation inventory.
- [ ] No duplicate public FQN across published Framework artifacts.
- [ ] Framework clean reactor build and all tests.
- [ ] Runtime clean build and all tests against the selected Framework version.
- [ ] Dependency tree confirms the intended API artifacts and no accidental duplicate classes.
- [ ] Contract tests cover graph and linear execution separately.
- [ ] Packaging/classpath test loads the runtime with both execution API modules present.
- [ ] Public API diff and migration notes reviewed.

## 6. Explicitly deferred

Do not combine this migration with checkpoint-store consolidation, lifecycle semantics changes, broad dependency cleanup or Quarkus adapter extraction. Those are separate changes with separate compatibility and validation requirements.

## 7. Evidence limitations

The inventory above uses GitHub code-search results and source-file inspection on the default branch as of 2026-11-01. The repository search index is not a substitute for a complete local checkout/grep. No Maven build, dependency-tree resolution or runtime class-loading test was executed as part of this documentation change.
