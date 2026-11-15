# M0 — Andalus Foundation Audit and Implementation Plan

- **Status:** In progress; source-level findings recorded, repository-wide verification pending.
- **Scope:** `andalus-framework` first, with downstream compatibility checks against `andalus`.
- **Decision:** Do not introduce a new generic domain-extension module, plugin manager, agent registry, or delegation API during M0.
- **Purpose:** Establish one coherent, portable foundation that independent domain products can embed or connect to without making Andalus domain-aware.

## 1. Target architecture and ownership

| Layer | Owns | Must not own |
|---|---|---|
| Andalus Framework | Portable agent/tool/skill/workflow contracts, extension SPIs, execution semantics, reusable orchestration, protocol-neutral metadata | Runtime deployment, product-specific business workflows, tenant authority, hosted marketplace |
| Andalus Runtime | Runtime composition, concrete lifecycle implementations, policy enforcement, persistence, transport endpoints, deployment and operations | Cifr/Farabi domain semantics or canonical product state |
| Domain product (e.g. Cifr/Farabi) | Domain agent loop, domain tools/use cases, domain authorization, domain state and artifacts | Generic runtime infrastructure that should be reusable |
| Interoperability adapters | MCP/A2A/HTTP/gRPC boundary translation | Canonical internal domain model |

The same product may be integrated as selected tools, as a specialist agent, or as a complete independently running application. These are complementary surfaces, not mutually exclusive product types.

## 2. Verified source-level findings

These findings come from inspecting the current default-branch files listed below. They are not a complete repository-wide reference graph and do not establish a successful build.

### 2.1 Existing foundations to reuse

- `docs/ECOSYSTEM_ARCHITECTURE.md` already states that the framework owns portable metadata/SPIs and runtime owns discovery endpoints, installation orchestration and deployment.
- `andalus-spi`, `andalus-plugin-spi`, `andalus-extension`, `andalus-plugin`, `andalus-registry`, `andalus-agent`, `andalus-tool`, `andalus-execution-api`, `andalus-execution-core`, `andalus-communication-api`, `andalus-protocol-local`, `andalus-a2a` and `andalus-mcp` already provide relevant building blocks.
- Existing `PluginRegistry`, `PluginManager`, plugin lifecycle, agent metadata and protocol-specific registries should be evaluated and composed rather than replaced with another generic registry.
- Existing execution and checkpoint overlap is already documented in `CONTRACT_INVENTORY.md`, `CONTRACT_CONSUMER_MAP.md`, ADR-0001 and ADR-0002. Continue those migration paths; do not add competing APIs.

### 2.2 Concrete inconsistencies and risks

| Finding | Evidence inspected | Required action |
|---|---|---|
| Plugin SPI artifact has runtime/framework dependencies | `framework/andalus-plugin/pom.xml` declares Quarkus Core, Mutiny and JBoss Logging | Determine which types actually require these dependencies. Move implementation/logging/CDI concerns to an implementation adapter where possible; keep portable contracts framework-neutral. |
| Base SPI also carries reactive and serialization dependencies | `framework/andalus-spi/pom.xml` declares Mutiny, Jackson databind and YAML, and depends on `andalus-extension` | Map public signatures and usages. Split only if dependency analysis proves these are not foundational contract requirements; avoid speculative module proliferation. |
| Java baseline is inconsistent in documentation | Root `pom.xml` sets `maven.compiler.release=25`; README advertises Java 21+ | Choose and test the actual supported baseline. Align README, parent properties, module compiler properties, CI and runtime compatibility matrix. |
| Parent build imports Quarkus BOM globally | Root `pom.xml` imports `quarkus-bom` | Decide whether this is acceptable dependency management only or causes unnecessary coupling. Prefer BOM imports in implementation modules or an optional integration BOM if portable consumers should not inherit Quarkus version management. |
| Duplicate execution-context and checkpoint contracts | `CONTRACT_INVENTORY.md`, `CONTRACT_CONSUMER_MAP.md`, ADR-0001 and ADR-0002 | Complete FQN/import/implementation/call-site mapping across Framework and Runtime before coordinated migration. |
| Execution lifecycle contract mismatch | `CONTRACT_CONSUMER_MAP.md` reports `DefaultExecutionContext.transition` writes lifecycle state without validating transitions despite documented expectations | Add a transition conformance test and enforce the approved transition table in the implementation. |
| Existing extension architecture is broader than a simple plugin loader | `docs/ECOSYSTEM_ARCHITECTURE.md` separates catalog, artifact store, trust, local registry, resolver, lifecycle and runtime | Preserve those boundaries. Marketplace/distribution is not part of the initial embedded extension lifecycle. |

### 2.3 Important precision

- The inspected POM proves declared dependencies, not the exact transitive dependency impact of every published artifact.
- The duplicate FQNs and checkpoint overlaps are documented in existing ADRs; this audit does not claim to have completed all consumer searches.
- The Java 25 compiler release and Java 21+ README badge are a documentation/build configuration inconsistency. The minimum supported JDK still requires CI and source/API compatibility verification.
- No Maven build, test suite, dependency tree or runtime integration test was executed as part of this source-only audit.

## 3. Canonical responsibilities to settle in M0

### Agent and extension model

Reuse existing contracts and decide one authoritative path for:
- Agent identity and descriptor metadata.
- Capability declarations and discovery.
- Extension identity, version, compatibility and lifecycle.
- Registration and invocation.
- Tool discovery and invocation.
- Failure, timeout and shutdown behavior.

Do not conflate a plugin (installable/lifecycle-managed extension package), an agent (executable specialist), and a tool (bounded operation). A plugin may contribute agents/tools/providers, but these concepts should not collapse into one type.

### Execution and delegation

The framework should define portable invocation semantics; runtime supplies concrete execution and persistence. The canonical invocation contract must define:
- Stable execution and correlation identifiers.
- Trusted principal/tenant context supplied by the host, not by model prompts.
- Allowed capabilities and policy context.
- Deadline, resource budget and cancellation.
- Typed input/output and structured failures.
- Progress/event semantics.
- Idempotency and retry expectations.
- Artifact references and terminal outcome.
- Checkpoint/resume semantics where supported.

A specialist domain agent owns its internal domain loop. Andalus coordinates a bounded invocation and observes progress/outcome; it does not micromanage every domain operation.

### Registry and discovery

Keep separate registries where their semantics differ (plugins, local agents, A2A peers, MCP tools). Define the canonical metadata contract and explicit projections/adapters. Discovery is not authorization, installation, or activation.

## 4. Required M0 audit work

- [ ] Enumerate all modules from the parent and nested POMs; record artifact IDs and source roots.
- [ ] Generate the Maven reactor and dependency graph; identify cycles, unused modules, duplicate dependencies and unexpected framework/runtime dependencies.
- [ ] Run repository-wide search for duplicate public FQNs, descriptor types, plugin/extension managers, registry contracts, execution contexts and checkpoint stores.
- [ ] Map every public contract to implementations, consumers, tests, CDI registrations, ServiceLoader entries and serialization boundaries.
- [ ] Inspect the runtime repository's Framework version pins, Java baseline, dependency tree, public API consumers and runtime-specific adapters.
- [ ] Verify lifecycle, cancellation, timeout, error, retry, idempotency and checkpoint semantics from implementation and tests.
- [ ] Identify published artifact/version compatibility and whether any consumers require binary compatibility.
- [ ] Produce an approved contract ownership matrix and migration sequence.
- [ ] Record actual build/test commands and results; do not infer success from source inspection.

## 5. M0 deliverables

1. This audit, updated with evidence and completed checkboxes.
2. Module inventory and dependency-direction map.
3. Contract ownership and consumer/implementation map.
4. Framework ↔ Runtime compatibility matrix (JDK, framework version, Quarkus/BOM, transport and artifact versions).
5. ADR decisions for descriptor/capability ownership, extension lifecycle, and execution invocation/delegation boundary where existing ADRs do not already decide them.
6. A sequenced M1 implementation plan with acceptance tests and migration compatibility policy.

## 6. M1 implementation direction (provisional until M0 evidence is complete)

1. Consolidate existing portable contracts and lifecycle semantics; avoid new generic module names unless a clear ownership gap remains.
2. Make extension registration/discovery deterministic and version-aware using existing extension/plugin contracts.
3. Separate portable SPIs from Quarkus/CDI-specific lifecycle wiring where dependency inspection justifies it.
4. Add framework-level conformance tests for extension lifecycle, agent/tool registration, invocation context propagation, cancellation, failures and shutdown.
5. Publish a minimal extension authoring example that works without a hosted marketplace or mandatory MCP.
6. Keep MCP/A2A as adapters and preserve direct in-process Java invocation for first-party integrations.

## 7. Deployment strategy (later milestones)

| Mode | Mechanism | When to use |
|---|---|---|
| Embedded | Direct Java contracts and selected framework modules | Local/community/single-process use where JDK and dependency compatibility are proven |
| Local sidecar | Local IPC, stdio, gRPC or HTTP | Process isolation or independent lifecycle is needed |
| Remote | Versioned HTTP/gRPC APIs with streaming and cancellation | Team/Pro/enterprise and independently scaled workers |
| Interoperability | MCP/A2A adapters | External ecosystems and third-party clients |

The deployment mode must not change domain ownership or the semantic contract. Do not require Kubernetes, a broker or a hosted control plane for local use.

## 8. Exit criteria

M0 is complete only when the module/dependency and consumer maps are evidence-backed, Framework/Runtime compatibility is tested, ownership decisions are approved, and M1 can proceed without inventing a parallel abstraction system.

M1 is not authorized to make broad breaking changes until the migration plan, release compatibility policy and required runtime changes are explicit.


## 9. Follow-up source audit — 2026-11-15

This section records a targeted follow-up against the current default branches of Framework and Runtime. It updates the earlier first-pass findings where source inspection reveals that documentation and implementation have diverged.

### 9.1 ExecutionContext: graph rename is already present in source

The current Framework source contains:

- `framework/andalus-engine/src/main/java/tech/kayys/andalus/execution/GraphExecutionContext.java`.
- `ExecutionEngine`, `ExecutionNode`, `EdgeCondition`, `Planner`, `Reasoner` and `ExecutionService` use `GraphExecutionContext` in the inspected source search.
- The old engine source path `framework/andalus-engine/src/main/java/tech/kayys/andalus/execution/ExecutionContext.java` is absent (404 from the repository contents API).
- Invocation-scoped `ExecutionContext` remains in `andalus-execution-api`; `DefaultExecutionContext` and `DefaultAgentInvocation` use that contract.
- `TracingHelper` still accepts `GraphExecutionContext`, and `andalus-observability` dependency direction remains a separate concern.

**Correction to earlier audit documents:** ADR-0001, CONTRACT_INVENTORY.md, CONTRACT_CONSUMER_MAP.md and EXECUTION_CONTEXT_MIGRATION_PLAN.md still describe the graph rename as proposed or pending. The source already contains the new graph type and migrated several core signatures. Update those documents to distinguish completed source changes from remaining verification. Do not repeat the rename or introduce another graph-context contract.

Still unverified: complete cross-repository and external consumer scan, Framework and Runtime clean builds, binary compatibility, dependency resolution, runtime context propagation, and tests for linear versus graph execution.

### 9.2 CheckpointStore: duplicate FQN remains present

Current source search still finds two declarations with the same FQN `tech.kayys.andalus.execution.CheckpointStore`:

- `framework/andalus-engine/src/main/java/tech/kayys/andalus/execution/CheckpointStore.java` — stores/restores engine `ExecutionSnapshot`.
- `framework/andalus-runtime-spi/src/main/java/tech/kayys/andalus/execution/CheckpointStore.java` — saves/loads runtime `AgentContext`.

The separately named `andalus-execution-api/.../execution/checkpoint/CheckpointStore` and `andalus-state-api/.../state/checkpoint/CheckpointStore` also remain distinct contracts. This is a confirmed source-level duplicate FQN; its exact resolved classpath/build failure mode has not been reproduced here.

**Priority:** complete the writer/reader/implementation/serialization map for each checkpoint path before renaming or deleting any contract. Preserve recovery behavior and stored-data compatibility.

### 9.3 Framework and Runtime build topology

- Framework root POM declares `maven.compiler.release=25` and imports the Quarkus BOM. Its nested `framework/pom.xml` imports the same Quarkus BOM again.
- The nested Framework aggregator lists 54 module entries; the `framework/` source directory currently contains additional directories not listed in that aggregator, so directory count must not be treated as active reactor-module count.
- Runtime root POM also declares `maven.compiler.release=25`. Its active reactor includes API, Runtime, feature-module and CLI modules; `runtime/andalus-agent-runner` and `cli/agent-cli` are commented out with documented reasons.
- Runtime Core directly depends on `andalus-execution-api`, `andalus-runtime-spi`, `andalus-engine`, `andalus-plugin-spi`, `andalus-spi`, `andalus-harness`, `andalus-a2a`, Quarkus extensions and other runtime integrations.
- Runtime root POM comments that Framework is now a standalone repository and must be installed before building Runtime. The selected Runtime Core POM pins Framework artifacts to `${project.version}` (0.0.1 through the parent), so the compatibility matrix must record the exact Framework commit/artifact revision used for each validation.

### 9.4 Search evidence and limitations

The follow-up used GitHub repository file fetches, directory listings and code search on the default branches. Search results are useful evidence but are not a complete symbol index. No checkout-wide grep, Maven dependency tree, duplicate-class scan, build, test, or runtime class-loading test was executed. These remain M0 exit criteria.


### 9.5 Checkpoint implementation evidence from targeted code search

The current Framework source search confirms:

- `andalus-execution-core/.../InMemoryExecutionCheckpointStore.java` implements the execution API contract in `tech.kayys.andalus.execution.checkpoint`. It is consumed by `DefaultRecoveryManager`, `DefaultExecutionMobility` and `DefaultMigrationCoordinator`; tests cover sequencing, integrity, restoration and recovery reconciliation.
- `andalus-state-core/.../InMemoryCheckpointStore.java` implements the state API contract in `tech.kayys.andalus.state.checkpoint`; state-core tests exercise save and handoff behavior.
- The two same-FQN contracts in `andalus-engine` and `andalus-runtime-spi` remain separate from those two in-memory implementations. The existing contract map records Runtime `AgentExecutionService` injection of the runtime-SPI contract, but the complete Runtime writer/reader and persisted-payload map has not been independently reproduced in this follow-up.

**Migration implication:** do not treat the execution API and state API stores as duplicates merely because both are named `CheckpointStore`; their packages and payload semantics differ. The immediate collision to eliminate is the shared FQN between engine snapshot storage and runtime AgentContext storage. Before changing either, identify whether engine snapshots are still actively written/read, and map the runtime AgentContext snapshot lifecycle and serialization format. Then choose adapters or an explicitly versioned replacement without losing resume/recovery behavior.


### 9.6 Engine snapshot path: current source behavior and defects

The direct source inspection of `andalus-engine` clarifies that the engine snapshot path is not merely an unused interface:

- `SnapshotManager` creates and retains `ExecutionSnapshot` instances in local `ConcurrentHashMap` instances. This is process-local memory, not durable persistence, and is not an implementation of the engine `CheckpointStore` interface.
- Repository code search found no implementation or call site for the engine `CheckpointStore` interface beyond its declaration; this is a targeted search result, not proof that no reflective/external consumer exists.
- The engine `CheckpointStore.getLatest(UUID)` default implementation returns `null`, which is unsafe as a meaningful default for a recovery port.
- `ExecutionSnapshot.Builder.build()` currently constructs a new snapshot using only execution ID and state. It silently ignores the builder's supplied snapshot ID and creation timestamp. `SnapshotManager.createSnapshot` sets both fields on the builder, so the resulting object does not preserve the requested ID/time.
- `SnapshotManager` stores each execution's history in mutable `ArrayList` values inside a `ConcurrentHashMap`; the map does not make those lists thread-safe. Concurrent snapshot creation/deletion/retention for the same execution can race.
- Snapshot retention removes entries from the global snapshot map while iterating and then edits per-execution history lists. Its concurrency behavior and retention correctness need dedicated tests.
- `ExecutionState` carries node status/results and variables, but it is not equivalent to the richer `ExecutionCheckpoint` envelope (attempt, sequence, payload bytes, manifest integrity and metadata).

**Implication:** there are currently at least three different execution recovery concepts: (a) engine in-memory snapshots managed by `SnapshotManager`, (b) a declared but apparently unimplemented engine persistence port, and (c) the execution-core checkpoint envelope with an in-memory store used by recovery/mobility. These should not be collapsed until the intended runtime semantics and payload mapping are agreed. The immediate defects in builder correctness and thread-safety should be tracked as implementation bugs independently of the naming collision.

### 9.7 Updated checkpoint audit conclusion

The current evidence supports this provisional direction:

1. Keep `andalus-execution-api` as the candidate durable execution checkpoint port; it already defines atomic append, strictly increasing sequence, immutable payload and manifest-integrity requirements.
2. Keep `andalus-state-api` as a separate state/provenance checkpoint abstraction unless a specific consumer proves the semantics are redundant.
3. Do not use the engine's current `CheckpointStore` as a persistence API without fixing its incomplete default behavior and defining an actual adapter.
4. Rename/move the runtime-SPI `AgentContext` store to a non-colliding runtime-owned type only after the current Runtime branch's implementation, CDI injection and serialization path are verified.
5. Treat `SnapshotManager` as a local snapshot utility, not as durable recovery. Either explicitly scope it as such or implement it over a durable port with well-defined consistency and retention behavior.

The Runtime main-branch source paths searched during this follow-up did not resolve to the older `runtime/andalus-runtime-core/.../AgentExecutionService.java` path recorded in the prior consumer map. The current Runtime repository layout has changed; its current AgentContext checkpoint implementation must be rediscovered from the live module tree before making claims about active writes/reads. The earlier Runtime consumer-map row is therefore historical evidence, not a freshly verified current-main call path.
